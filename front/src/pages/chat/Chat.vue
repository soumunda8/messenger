<template>
  <div>
    <left-side-view />
    <div id="right">
      <div class="content chat">
        <input type="hidden" v-model="enterId" />
        <drawing-form ref="drawingForm" v-show="showCanvas" @toggle-canvas="toggleCanvas" @canvas-prepared="sendCanvas" />
        <chatting-list v-bind:chatList="chatList" @toggle-canvas="toggleCanvas" />
        <chatting-form ref="chattingForm" @toggle-canvas="toggleCanvas" @submitChat="submitChat" @file-prepared="sendFile" :uploadStatus="uploadStatus" />
      </div>
    </div>
  </div>
</template>

<script>
import LeftSideView from '@/components/layout/LeftSideView.vue'
import ChattingForm from '@/components/chat/ChattingForm.vue'
import ChattingList from '@/components/chat/ChattingList.vue'
import DrawingForm from '@/components/chat/DrawingForm.vue'
import api from '@/api'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { mapState } from 'vuex'

export default {
  name: 'Chat',
  components: {
    LeftSideView,
    ChattingForm,
    ChattingList,
    DrawingForm
  },
  computed: {
    ...mapState(['me'])
  },
  data () {
    return {
      stompClient: null,
      enterId: '',
      chatList: [],
      uploadStatus: null,
      showCanvas: false,
      apiUrl: process.env.VUE_APP_URL,
      apiPort: process.env.VUE_SERVER_PORT,
      canvasBackgroundImage: ''
    }
  },
  mounted () {
    this.fetchChatList()
    this.connect()
  },
  methods: {
    fetchChatList () {
      const userid = this.me.userId
      const roomid = this.$route.params.roomId

      api.post('/getChatList', {userid, roomid})
        .then(res => {
          this.chatList = res.data
        })

      api.post('/getEnterId', {userid, roomid})
        .then(res => {
          this.enterId = res.data
        })
    },
    connect () {
      const roomid = this.$route.params.roomId
      const socket = new SockJS(`${this.apiUrl}:${this.apiPort}/ws-stomp`)
      this.stompClient = Stomp.over(socket)
      this.stompClient.debug = null

      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/chat/' + roomid, message => {
          const chatData = JSON.parse(message.body)
          this.chatList.push(chatData)
        })
      })
    },
    submitChat (payload) {
      const { message } = payload
      const roomid = this.$route.params.roomId
      const id = this.enterId
      const senderid = this.me.userId
      const sendernm = this.me.userNm
      api.post('/chat/send', {message, roomid, id, senderid, sendernm})
        .then(res => {
          this.$refs.chattingForm.clearMessage()
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
    },
    sendFile (fileData) {
      const formData = new FormData()
      formData.append('roomId', this.$route.params.roomId)
      formData.append('enterId', this.enterId)
      formData.append('userId', this.me.userId)
      formData.append('userNm', this.me.userNm)
      formData.append('uploadFile', fileData.files[0])
      api.post('/chat/sendFile', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(res => {
        this.uploadStatus = 'success'
      }).catch(error => {
        console.error('Error uploading file : ', error.response.data)
      })
    },
    sendCanvas (canvasData) {
      const formData = new FormData()
      formData.append('roomId', this.$route.params.roomId)
      formData.append('enterId', this.enterId)
      formData.append('userId', this.me.userId)
      formData.append('userNm', this.me.userNm)
      formData.append('imageUrl', canvasData.imageUrl)
      api.post('/chat/sendDraw', formData)
        .then(res => {
          this.toggleCanvas()
        }).catch(error => {
          console.error('Error uploading file : ', error.response.data)
        })
    },
    toggleCanvas (imgSrc) {
      this.showCanvas = !this.showCanvas
      if (this.showCanvas) {
        this.$nextTick(() => {
          if (this.$refs.drawingForm) {
            this.$refs.drawingForm.updateCanvasSize()
            if (imgSrc) {
              this.$refs.drawingForm.setCanvasBackground(imgSrc)
            }
          }
        })
      }
    }
  },
  beforeDestroy () {
    this.disconnect()
  }
}
</script>
