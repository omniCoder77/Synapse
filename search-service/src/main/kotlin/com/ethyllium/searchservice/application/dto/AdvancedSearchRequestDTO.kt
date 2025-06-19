package com.ethyllium.searchservice.application.dto

data class AdvancedSearchRequestDTO(
    val query: String? = null,
    val must: List<SearchConditionDTO> = emptyList(),
    val should: List<SearchConditionDTO> = emptyList(),
    val range: SearchRangeDTO? = null,
    val filters: Map<String, List<String>> = emptyMap()
) {
    fun toFiltersMap(): Map<String, List<String>> {
        val map = mutableMapOf<String, List<String>>()
        filters.forEach { (key, value) -> map[key] = value }
        
        must.forEach { condition ->
            map[condition.field] = map.getOrDefault(condition.field, emptyList()) + "must:${condition.value}"
        }
        
        should.forEach { condition ->
            map[condition.field] = map.getOrDefault(condition.field, emptyList()) + "should:${condition.value}"
        }
        
        range?.let {
            map[it.field] = listOf("gte:${it.gte}", "lte:${it.lte}")
        }
        
        return map
    }
}

data class SearchConditionDTO(
    val field: String,
    val value: String
)

data class SearchRangeDTO(
    val field: String,
    val gte: Long? = null,
    val lte: Long? = null
)