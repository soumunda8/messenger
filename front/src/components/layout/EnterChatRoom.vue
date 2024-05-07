<template>
<ul id="chatListArea">
  <li v-for="(chat, index) in enterChat" :key="chat.id">
    <a :href="`/chat/${chat.roomid}`" >{{ chat.roomnm }}</a>
    <div>
      <div :id="'chatPopup' + index">
        <button class='more' @click='openChatPopup(index)'>더보기</button>
        <div class='popup_area' v-show="isOpen(index)">
          <button class='close_btn' @click='closeChatPopup(index)'>닫기</button>
          <ul>
            <li>{{ chat.roomnm }}</li>
            <li><button class='btn' @click='outRoom(chat.id)'>대화방 나가기</button></li>
          </ul>
        </div>
      </div>
    </div>
  </li>
</ul>
</template>

<script>
import api from '@/api'
import { mapState } from 'vuex'

export default {
  name: 'EnterChatRoom',
  props: {
    enterChat: {
      type: Array
    }
  },
  computed: {
    ...mapState(['me'])
  },
  data () {
    return {
      openPopups: {}
    }
  },
  methods: {
    openChatPopup (index) {
      this.resetPopups()
      this.$set(this.openPopups, index, true)
    },
    closeChatPopup (index) {
      this.$set(this.openPopups, index, false)
    },
    resetPopups () {
      for (const key in this.openPopups) {
        this.$set(this.openPopups, key, false)
      }
    },
    isOpen (index) {
      return !!this.openPopups[index]
    },
    outRoom (id) {
      const sendernm = this.me.userNm
      if (confirm('방을 나가시면 모든 대화가 사라집니다.\n방을 나가시겠습니까?')) {
        api.post('/outRoom', {id, sendernm})
          .then(res => {
            this.$router.push({name: 'Enter'})
          })
      }
    }
  }
}
</script>
