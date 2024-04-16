import { USERID, USERNM } from './mutations-types'

export default {
  [USERID] (state, userId) {
    state.userId = userId
  },
  [USERNM] (state, userNm) {
    state.userNm = userNm
  }
}
