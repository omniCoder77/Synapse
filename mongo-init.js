db = db.getSiblingDB("admin");

db.createUser({
  user: "client",
  pwd: "client-password",
  roles: [
    { role: "readWrite", db: "product" },
    { role: "read", db: "local" }
  ]
});

db = db.getSiblingDB("product");
db.createCollection("outbox");
