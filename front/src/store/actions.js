import { SET_ACCESS_TOKEN, SET_MY_INFO } from './mutations-types'
import Vue from 'vue'
import api from '@/api'

export default {
  signin ({ commit }, payload) {
    const id = payload.userId
    const pw = payload.userPw
    return api.post('/api/login', {id, pw})
      .then(res => {
        Vue.prototype.$session.set('userId', res.data.userId)
        Vue.prototype.$session.set('userNm', res.data.userNm)
        const { accessToken } = res.data.accessToken
        commit(SET_ACCESS_TOKEN, accessToken)
        // return api.get('/api/users/me')
      }).then(res => {
        commit(SET_MY_INFO, res.data)
      })
  }
}
