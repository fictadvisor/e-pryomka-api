const express = require('express');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');

const swaggerDocument = YAML.load('./openapi.yml');

const app = express();

app.use('/', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
app.listen(3004, () => console.log('Swagger runs on port 3004'))
