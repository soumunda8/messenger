import { SET_ACCESS_TOKEN, SET_MY_INFO } from './mutations-types'
import api from '@/api'

export default {
  [SET_ACCESS_TOKEN] (state, accessToken) {
    if (accessToken) {
      state.accessToken = accessToken
      api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
    }
  },
  [SET_MY_INFO] (state, me) {
    if (me) {
      state.me = me
    }
  }
}
