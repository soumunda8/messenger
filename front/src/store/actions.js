import { USERID, USERNM, SET_ACCESS_TOKEN } from './mutations-types'
import api from '@/api'

export default {
  singin ({ commit }, payload) {
    const id = payload.userId
    const pw = payload.userPw
    return api.post('/api/login', {id, pw})
      .then(res => {
        commit(USERID, id)
        const userNm = res.data.username
        commit(USERNM, userNm)
        const accessToken = res.data.token
        commit(SET_ACCESS_TOKEN, accessToken)
      })
  }
}
