'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  VUE_APP_URL: '"http://192.168.56.1"',
  VUE_CLIENT_PORT: '3000',
  VUE_SERVER_PORT: '8086'
})
