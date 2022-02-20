const express = require('express');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');

const swaggerDocument = YAML.load('./openapi.yml');

const app = express();

app.use('/', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
app.use((req, res) => {
    res.header("Access-Control-Allow-Origin", "*")
})

app.listen(3004, () => console.log('Swagger runs on port 3000'))
