import axios from 'axios'

const vueAppUrl = process.env.VUE_APP_URL || 'http://localhost'
const parsedUrl = new URL(vueAppUrl)
const host = parsedUrl.hostname
const serverPort = process.env.VUE_SERVER_PORT || 8086

export default axios.create({
  baseURL: `//${host}:${serverPort}`
})
