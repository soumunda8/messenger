<template>
  <div>
    <left-side-view />
    <div id="right">
      <div class="content chat">
        <input type="hidden" v-model="enterId" />
        <chatting-list :chatList="chatList" />
        <chatting-form @submitChat="submitChat" />
      </div>
    </div>
  </div>
</template>

<script>
import LeftSideView from '@/components/layout/LeftSideView.vue'
import ChattingForm from '@/components/chat/ChattingForm.vue'
import ChattingList from '@/components/chat/ChattingList.vue'
import api from '@/api'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export default {
  name: 'Chat',
  components: {
    LeftSideView,
    ChattingForm,
    ChattingList
  },
  data () {
    return {
      stompClient: null,
      enterId: '',
      chatList: []
    }
  },
  mounted () {
    const userid = this.$session.get('userId')
    const roomid = this.$route.params.roomId

    api.post('/getChatList', {userid, roomid})
      .then(res => {
        this.chatList = res.data
      })

    api.post('/getEnterId', {userid, roomid})
      .then(res => {
        this.enterId = res.data
      })

    this.connect()
  },
  methods: {
    connect () {
      const roomid = this.$route.params.roomId
      const socket = new SockJS('http://localhost:8086/ws-stomp')
      this.stompClient = Stomp.over(socket)
      this.stompClient.debug = null

      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/api/chat/' + roomid, message => {
          const chatData = JSON.parse(message.body)
          this.chatList.push(chatData)
        })
      })
    },
    submitChat (payload) {
      const { message } = payload
      const roomid = this.$route.params.roomId
      const id = this.enterId
      const senderid = this.$session.get('userId')
      const sendernm = this.$session.get('userId')
      api.post('/chat/send', {message, roomid, id, senderid, sendernm})
        .then(res => {
          // Handle response
        })
        .catch(error => {
          console.error('Error sending message:', error)
        })
    },
    disconnect () {
      if (this.stompClient !== null) {
        this.stompClient.disconnect()
      }
      console.log('Disconnected')
    }
  },
  beforeDestroy () {
    this.disconnect()
  }
}
</script>
