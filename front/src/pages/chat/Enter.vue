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

export default {
  name: 'Chat',
  components: {
    LeftSideView,
    EnterRoomForm
  },
  methods: {
    onclick (payload) {
      const roomid = payload.roomid
      const senderid = this.$session.get('userId')
      const sendernm = this.$session.get('userNm')
      api.post('/enterRoom', {roomid, senderid, sendernm})
        .then(res => {
          this.$router.push({name: 'Chat', params: { roomId: roomid }})
        })
    }
  }
}
</script>
