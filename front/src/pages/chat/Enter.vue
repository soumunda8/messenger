<template>
  <div>
    <left-side-view />
    <enter-room-form @enterRoom="onclick" />
  </div>
</template>

<script>
import LeftSideView from '@/components/layout/LeftSideView.vue'
import EnterRoomForm from '@/components/chat/EnterRoomForm.vue'
import api from '@/api'
import { mapState } from 'vuex'

export default {
  name: 'Chat',
  computed: {
    ...mapState(['me'])
  },
  components: {
    LeftSideView,
    EnterRoomForm
  },
  methods: {
    onclick (payload) {
      const roomid = payload.roomid
      const senderid = this.me.userId
      const sendernm = this.me.userNm
      api.post('/enterRoom', {roomid, senderid, sendernm})
        .then(res => {
          if (res.data) {
            this.$router.push({name: 'Chat', params: { roomId: roomid }})
          } else {
            alert('잘못된 코드입니다.\n다시 확인해주세요')
          }
        })
    }
  }
}
</script>
