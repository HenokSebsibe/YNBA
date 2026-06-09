# ExpenseIQ API & Database Testing Guide

This document explains how to test the ExpenseIQ backend API and database using Postman.
It includes setup steps, recommended workflows, and practical Postman techniques.

## Prerequisites

- Node.js installed
- MySQL server running locally or accessible remotely
- Postman installed
- Backend dependencies installed under `backend/`

## 1. Start the Backend Server

1. Open a terminal and navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Install dependencies if not already installed:
   ```bash
   npm install
   ```
3. Start the server:
   - Development mode:
     ```bash
     npm run dev
     ```
   - Production mode:
     ```bash
     npm start
     ```
4. Confirm the server is running by visiting the health check endpoint in Postman or browser:
   ```http
   GET http://localhost:3000/api/health/status
   ```

## 2. Prepare the Database

1. Ensure MySQL is running.
2. Create or import the database schema using the SQL scripts in `backend/database/`.
   Example using the MySQL CLI:
   ```sql
   SOURCE backend/database/schema.sql;
   ```
3. Confirm the database connection values in the backend configuration.
   - Check `.env` or the database config file under `backend/config/`.
4. Restart the backend server after any database configuration changes.

## 3. Test API Endpoints with Postman

### 3.1 Create a Postman Collection

- Create a new collection called `ExpenseIQ API`.
- Add requests for each API route you want to test.

### 3.2 Organize requests by feature

Example collection structure:
- Health
  - GET `/api/health/status`
- Auth
  - POST `/api/auth/login`
  - POST `/api/auth/register`
- Users
  - GET `/api/users`
  - POST `/api/users`
- Companies
  - GET `/api/companies`
  - POST `/api/companies`
- Expenses
  - GET `/api/expenses`
  - POST `/api/expenses`
- Incomes
  - GET `/api/incomes`
  - POST `/api/incomes`

### 3.3 Use Environments and Variables

Create a Postman environment with variables such as:
- `{{baseUrl}}` → `http://localhost:3000`
- `{{authToken}}`

Then use these variables in requests:
```http
GET {{baseUrl}}/api/users
```

### 3.4 Set request headers

For JSON APIs, add headers:
- `Content-Type: application/json`
- `Accept: application/json`
- `Authorization: Bearer {{authToken}}` (when needed)

### 3.5 Add request bodies

When creating or updating resources, send JSON in the body.
Example for creating a user:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password123",
  "company_id": 1,
  "role_id": 2
}
```

### 3.6 Test login with JSON/raw body and inspect user info

1. Create a `POST` request to the login endpoint, for example:
   ```http
   POST {{baseUrl}}/api/auth/login
   ```
2. In the Body tab, choose `raw` and `JSON`.
3. Send this payload as raw JSON:
   ```json
   {
     "email": "john@example.com",
     "password": "Password123"
   }
   ```
4. Use headers:
   - `Content-Type: application/json`
   - `Accept: application/json`
5. Check the response for:
   - `token` or `accessToken`
   - `user` object or user details
   - success status code (`200` or `201`)
6. Example expected response body:
   ```json
   {
     "token": "<jwt-token>",
     "user": {
       "id": 1,
       "name": "John Doe",
       "email": "john@example.com",
       "role_id": 2,
       "company_id": 1
     }
   }
   ```
7. Store the token in an environment variable with a test script:
   ```js
   var jsonData = pm.response.json();
   pm.environment.set("authToken", jsonData.token);
   pm.environment.set("currentUserId", jsonData.user.id);
   pm.environment.set("currentUserEmail", jsonData.user.email);
   ```
8. Use the returned user info to verify the login payload and to drive later requests.

### 3.7 Run requests and verify responses

- Send the request.
- Check status codes: `200`, `201`, `400`, `401`, `404`, etc.
- Inspect response JSON and confirm expected fields.
- Confirm the database changed correctly when a create/update/delete request is successful.

## 4. Test Database State with Postman

### 4.1 Use API Endpoints as the database test surface

Because Postman is an API testing tool, the recommended way to test the database is via the backend API endpoints.

Example workflow:
1. Send a request to create a record.
2. Send a request to read the same record.
3. Optionally send an update request.
4. Send a delete request.
5. Confirm the record no longer exists.

### 4.2 Verify database changes indirectly

- After a successful POST, use GET endpoints to confirm the new entry exists.
- After a successful PUT or PATCH, use GET to confirm the values were updated.
- After a DELETE request, use GET to confirm the record is gone.

### 4.3 Use request tests in Postman

Add tests to assert responses automatically.
Example test script:
```js
pm.test("Status code is 201", function () {
  pm.response.to.have.status(201);
});
pm.test("Response contains id", function () {
  var jsonData = pm.response.json();
  pm.expect(jsonData).to.have.property("id");
});
```

### 4.4 Use collection runner for repeated database checks

- Use the Collection Runner to execute a sequence of requests automatically.
- Combine requests for create → read → update → delete.
- Use environment variables to store generated IDs between requests.

## 5. Advanced Postman Testing Techniques

### 5.1 Use pre-request scripts

Set or modify variables before a request runs.
Example:
```js
pm.environment.set("contentType", "application/json");
```

### 5.2 Store and reuse authentication tokens

1. Login via `/api/auth/login`.
2. Extract the token in a test script:
```js
var jsonData = pm.response.json();
pm.environment.set("authToken", jsonData.token);
```
3. Use `{{authToken}}` in later requests.

### 5.3 Run smoke tests

Create one collection with essential endpoints that verify core functionality:
- Health check
- Login
- Create/read/update/delete a user or company
- Query a list endpoint 

### 5.4 Validate database behavior with API flows

Use Postman to verify business rules and database constraints.
Example validations:
- Required fields are enforced
- Unique email or username constraints work
- Related records are created/deleted consistently

## 6. Other Ways to Test Database Directly

Though Postman is best for API testing, direct database checks are also useful:
- Use MySQL Workbench, DBeaver, or CLI to run SQL queries directly.
- Inspect tables after API operations:
  ```sql
  SELECT * FROM users WHERE email = 'john@example.com';
  ```
- Run schema or migration SQL scripts manually if needed.

## 7. Example Postman Test Flow

1. Start backend server.
2. Import Postman environment variables:
   - `{{baseUrl}} = http://localhost:3000`
   - `{{authToken}} = ""`
3. Send `POST {{baseUrl}}/api/auth/login` to get a token.
4. Save `authToken` from response.
5. Send `POST {{baseUrl}}/api/users` to create a user.
6. Send `GET {{baseUrl}}/api/users` to confirm the user exists.
7. Send `PUT {{baseUrl}}/api/users/:id` to update the user (if supported).
8. Send `DELETE {{baseUrl}}/api/users/:id` to remove the user.
9. Use `GET {{baseUrl}}/api/users/:id` to verify deletion.

## 8. Tips & Best Practices

- Keep Postman collections and environments under version control when possible.
- Add descriptive names to each request.
- Use tests to automate response verification.
- Reset the database state between test runs if needed.
- Use a separate test database if you plan to run destructive tests.

---

This README provides a practical path for testing the ExpenseIQ API and validating database behavior with Postman. Adjust the endpoint names to match your actual backend routes and payloads as needed.