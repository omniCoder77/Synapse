package com.ethyllium.searchservice.infrastructure.adapter

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.*
import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.domain.service.ProductSearchService
import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@CacheConfig(cacheNames = ["products"])
class ProductSearchServiceAdapter(
    private val reactiveElasticsearchOperations: ReactiveElasticsearchOperations
) : ProductSearchService {

    @Cacheable(
        key = "{#query, #category, #brand?.toString(), #minPrice, #maxPrice, #status?.toString(), #tags?.toString(), #color?.toString(), #pageable.pageNumber, #pageable.pageSize}",
    )
    override fun searchProducts(
        query: String?,
        category: String?,
        brand: List<String>?,
        minPrice: Double?,
        maxPrice: Double?,
        status: List<String>?,
        tags: List<String>?,
        color: List<String>?,
        pageable: Pageable
    ): Mono<Page<SearchProduct>> {
        val boolQuery = BoolQuery.Builder()

        query?.let {
            boolQuery.must(
                MultiMatchQuery.Builder().query(it).fields("name^2", "description^1.5", "shortDescription^1")
                    .type(TextQueryType.BestFields).fuzziness("AUTO").build()._toQuery()
            )
        }

        val filters = mutableListOf<Query>()
        category?.let { cat ->
            filters.add(MatchPhraseQuery.of { mp ->
                mp.field("category.name").query(cat)
            }._toQuery())
        }

        brand?.takeIf { it.isNotEmpty() }?.let { brandList ->
            filters.add(TermsQuery.of { t ->
                t.field("brand.name").terms { it.value(brandList.map { f -> FieldValue.of(f) }) }
            }._toQuery())
        }

        status?.takeIf { it.isNotEmpty() }?.let { statusList ->
            filters.add(TermsQuery.of { t ->
                t.field("status").terms { it.value(statusList.map { f -> FieldValue.of(f) }) }
            }._toQuery())
        }

        tags?.takeIf { it.isNotEmpty() }?.let { tagList ->
            filters.add(TermsQuery.of { t ->
                t.field("tags").terms { it.value(tagList.map { f -> FieldValue.of(f) }) }
            }._toQuery())
        }

        color?.takeIf { it.isNotEmpty() }?.let { colorList ->
            filters.add(TermsQuery.of { t ->
                t.field("specifications.color").terms { it.value(colorList.map { f -> FieldValue.of(f) }) }
            }._toQuery())
        }

        if (minPrice != null || maxPrice != null) {
            filters.add(RangeQuery.of { r ->
                r.number { s ->
                    s.field("pricing.basePrice")
                    minPrice?.let { s.gte(it) }
                    maxPrice?.let { s.lte(it) }
                }

            }._toQuery())
        }

        if (filters.isNotEmpty()) {
            boolQuery.filter(filters)
        }

        val nativeQuery = NativeQuery.builder().withQuery(boolQuery.build()._toQuery()).withPageable(pageable).build()

        val hits = reactiveElasticsearchOperations.search(nativeQuery, SearchProduct::class.java)
            .map(SearchHit<SearchProduct>::getContent).collectList()

        val total = reactiveElasticsearchOperations.count(nativeQuery, SearchProduct::class.java)

        return Mono.zip(hits, total).map { PageImpl(it.t1, pageable, it.t2) }
    }

    @Cacheable(key = "#id", unless = "#result == null")
    override fun getProductById(id: String): Mono<Product> {
        return reactiveElasticsearchOperations.get(id, Product::class.java)
    }

    @Cacheable(
        key = "{#productId, #limit}", unless = "#result == null || !#result.collectList().block().isEmpty()"
    )
    override fun findSimilarProducts(productId: String, limit: Int): Flux<Product> {
        val moreLikeThisQuery = MoreLikeThisQuery.of { mlt ->
            mlt.fields("name", "description")
                .like(Like.of { l -> l.document { d -> d.index("products").id(productId) } }).minTermFreq(1)
                .maxQueryTerms(12)
        }._toQuery()

        val nativeQuery =
            NativeQuery.builder().withQuery(moreLikeThisQuery).withPageable(Pageable.ofSize(limit)).build()

        return reactiveElasticsearchOperations.search(nativeQuery, SearchProduct::class.java)
            .map { it.content.toDomain() }
    }

    @Cacheable(
        key = "{#limit, #timespan}", unless = "#result == null || !#result.collectList().block().isEmpty()"
    )
    override fun getPopularSearches(limit: Int, timespan: String): Flux<Pair<String, Int>> {
        val query = Query.of { q -> q.matchAll { it } }
        val nativeQuery = NativeQuery.builder().withQuery(query).withPageable(Pageable.ofSize(limit)).withSort(
            Sort.by(
                Sort.Direction.DESC, "analytics.views"
            )
        ).build()
        return reactiveElasticsearchOperations.search(nativeQuery, SearchProduct::class.java)
            .map { it.content.name to (it.content.analytics.views.toInt()) }
    }
}