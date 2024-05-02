import { USERID, USERNM, SET_ACCESS_TOKEN } from './mutations-types'
import api from '@/api'

export default {
  [USERID] (state, userId) {
    this.$session.set('userId', userId)
    state.userId = userId
  },
  [USERNM] (state, userNm) {
    this.$session.set('userNm', userNm)
    state.userNm = userNm
  },
  [SET_ACCESS_TOKEN] (state, accessToken) {
    if (accessToken) {
      state.accessToken = accessToken
      api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
    }
  }
}
