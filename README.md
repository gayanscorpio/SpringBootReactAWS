curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "devuser", "password": "dev123", "role": "DEV"}'


 curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "adminuser", "password": "admin123", "role": "ADMIN"}'



curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "devuser", "password": "dev123"}'


curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "adminuser", "password": "admin123"}'



curl -i  -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com"
}'



curl -i -X GET http://localhost:8080/students -H "Authorization: Bearer " -H "Accept: application/json"



 curl -i -X DELETE http://localhost:8080/students/2 \
  -H "Authorization: Bearer " \
 -H "Accept: application/json"




——————————————————————————————————————————————————

curl -v -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>" \
  -d '{"query": "{ allStudents(page: 0, size: 5) { id name email } }"}'




curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"query": "query { studentById(id: \"123\") { id name email } }"}'


curl -i -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "query": "mutation { addStudent(name: \"John Doe\", email: \"john.doe@example.com\") { id name email } }"
}'





——————————————————————————

curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { allBooks { id title author isbn availableCopies totalCopies } }"}'


curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { bookById(id: 1) { title author } }"}'

{
  "query": "query getBook($id: ID!) { bookById(id: $id) { title author } }",
  "variables": { "id": 1 }
}

















