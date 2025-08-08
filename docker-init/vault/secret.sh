#!/bin/bash
export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=8cc7f295-45aa-4871-9b25-5db24282e27c
vault secrets enable transit
vault kv put secret/auth-service/jwt   key-material="Uk1WZxmna+axYF5kENxcbc/dmdkt1qF1ZBgRtQVrAtI="   algorithm="HmacSHA256"
vault kv put secret/auth-service database.username="postgres"   database.password="postgres" twilio.account-sid="AC6e0e7cbef200b2fd85c5e8429768041e" twilio.auth-token="7ba2d4d11e2ddcf056b95240f2f29b77" twilio.path-service-id="VA02b136216de2b87c9c133d72381269e6"
vault kv put secret/search-service   database.host="elasticsearch"   database.port="9200"   database.username="elastic"   database.password="c9QobvxH" kafka.host="kafka"
vault kv put secret/payment-service   database.host="postgres"   database.port="5432"   database.name="payment_db"   database.username="postgres"   database.password="postgres" kafka.host="kafka" kafka.port="9092" razorpay.secret.key="599qZgN39dFYYS62aI4nUyXg" razorpay.key.id="rzp_test_LVfYqp2c4TtHRR" razorpay.webhook.secret="rishabh@123"
vault kv put secret/order-service   database.host="postgres"   database.port="5432"   database.name="order_db"   database.username="postgres"   database.password="postgres" kafka.host="kafka"
vault kv put secret/product-service database.host="mongodb" database.port="27017"   database.name="products" redis.host="redis" twilio.account-sid="AC6e0e7cbef200b2fd85c5e8429768041e" twilio.auth-token="7ba2d4d11e2ddcf056b95240f2f29b77" twilio.path-service-id="VA02b136216de2b87c9c133d72381269e6" kafka.host="kafka"