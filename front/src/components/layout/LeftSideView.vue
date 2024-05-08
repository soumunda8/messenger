<template>
    <div id="left">
        <div class="content">
            <div id="leftHeader">
                <h1>로고</h1>
                <div>
                    <div id="infoPopup">
                        <button class="more" @click="openPopup(1)">생성</button>
                        <div class="popup_area" v-show="showInfoPopup">
                            <button class="close_btn" @click="closePopup()">닫기</button>
                            <ul>
                                <li>
                                    <router-link :to="{ name: 'Create' }">채팅방 생성</router-link>
                                </li>
                                <li>
                                    <router-link :to="{ name: 'Enter' }">채팅방 입장</router-link>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div id="leftHome">
                <enter-chat-room :enterChat="enterChat" />
            </div>
            <div id="leftFooter">
                <div>
                    <div class="thumbnail"></div>
                    <p v-if="me && me.userId">{{ me.userId }}</p>
                </div>
                <div class="btn_area">
                    <div id="thumbnailPopup">
                        <button class="more" @click="openPopup(3)">더보기</button>
                        <div class="popup_area" v-show="showThumbnailPopup">
                            <button class="close_btn" @click="closePopup()">닫기</button>
                            <ul>
                                <li>
                                    <!--<router-link :to="{ name: 'Logout' }">로그아웃</router-link>-->
                                    <a @click="logoutPro" class="btn_point">로그아웃</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import EnterChatRoom from '@/components/layout/EnterChatRoom.vue'
import api from '@/api'
import { mapState, mapActions } from 'vuex'

export default {
  name: 'LeftSideView',
  components: {
    EnterChatRoom
  },
  computed: {
    ...mapState(['me'])
  },
  data () {
    return {
      enterChat: [],
      showInfoPopup: false,
      showThumbnailPopup: false
    }
  },
  created () {
    const id = this.me.userId

    api.post('/getMyChatList', {id})
      .then(res => {
        this.enterChat = res.data
      })
  },
  methods: {
    openPopup (num) {
      // 모든 팝업을 초기 상태로 숨김
      this.showInfoPopup = false
      this.showThumbnailPopup = false

      // num 값에 따라 특정 팝업을 표시
      switch (num) {
        case 1:
          this.showInfoPopup = true
          break
        case 3:
          this.showThumbnailPopup = true
          break
        default:
          alert('잘못된 접근입니다.')
          break
      }
    },
    closePopup () {
      this.showInfoPopup = false
      this.showThumbnailPopup = false
    },
    logoutPro () {
      api.post('/api/logout')
        .then(res => {
          this.signout()
          window.location.href = '/'
        })
        .catch(error => {
          console.error('Logout failed:', error)
        })
    },
    ...mapActions([ 'signout' ])
  }
}
</script>

<style scoped>
  .btn_point {cursor: pointer;}
</style>
