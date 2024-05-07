<template>
  <div>
    <left-side-view />
    <create-room-form @createRoom="onclick" />
  </div>
</template>

<script>
import LeftSideView from '@/components/layout/LeftSideView.vue'
import CreateRoomForm from '@/components/chat/CreateRoomForm.vue'
import api from '@/api'
import { mapState } from 'vuex'

export default {
  name: 'Create',
  computed: {
    ...mapState(['me'])
  },
  components: {
    LeftSideView,
    CreateRoomForm
  },
  methods: {
    onclick (payload) {
      const roomnm = payload.roomnm
      const userid = this.me.userId
      api.post('/createRoom', {userid, roomnm})
        .then(res => {
          this.$router.push({name: 'Chat', params: { roomId: res.data }})
        })
    }
  }
}
</script>
