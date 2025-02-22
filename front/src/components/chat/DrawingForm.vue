<template>
    <div id="canvasArea" ref="canvasArea">
        <div>
          <canvas ref="canvas" @mousedown="startDrawing" @mousemove="draw" @mouseup="stopDrawing" @mouseleave="stopDrawing" @touchstart="startDrawing" @touchmove="draw" @touchend="stopDrawing" @touchcancel="stopDrawing"></canvas>
        </div>
        <ul class="controlBtn bottom left">
            <li><button @click="triggerFileInput">배경</button></li>
            <!-- <li><button @click="shareDrawing">공유하기</button></li> -->
            <li><button :class="{ active: drawMode === 'line' }" @click="setDrawMode('line')">선</button></li>
            <li>
              <button :class="{ active: this.drawMode.startsWith('shape_')}" @click="toggleShapesMenu">도형</button>
              <ul v-show="showShapesMenu" style="display: none;position: absolute;padding-left: 0;">
                <li><button :class="{ active: this.drawMode === 'shape_circle' }" @click="setShape('circle')">○</button></li>
                <li><button :class="{ active: this.drawMode === 'shape_triangle' }" @click="setShape('triangle')">△</button></li>
                <li><button :class="{ active: this.drawMode === 'shape_square' }" @click="setShape('square')">□</button></li>
              </ul>
            </li>
            <li><button :class="{ active: drawMode === 'free' }" @click="setDrawMode('free')">자유</button></li>
            <li><button @click="removeAll">전체지우기</button></li>
        </ul>
        <ul class="controlBtn top right">
            <li>
                <input type="color" v-model="selectedColor">
            </li>
        </ul>
        <ul class="controlBtn bottom right">
            <li><button @click="sendCanvas">전송</button></li>
            <li><button @click="closeCanvas">종료</button></li>
        </ul>
        <input type="file" ref="fileInput" @change="handleFile" style="display: none;" accept="image/*">
    </div>
</template>

<script>
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
// import pako from 'pako'

export default {
  name: 'DrawingForm',
  data () {
    return {
      history: [],
      selectedColor: '#ff0000',
      drawing: false,
      context: null,
      lastX: 0,
      lastY: 0,
      drawMode: 'free',
      showShapesMenu: false,
      currentShape: null,
      canDraw: true,
      stompClient: null,
      roomId: this.$route.params.roomId,
      apiDrawUrl: process.env.VUE_APP_URL,
      apiDrawPort: process.env.VUE_SERVER_PORT
    }
  },
  mounted () {
    this.updateCanvasSize()
    window.addEventListener('resize', this.updateCanvasSize)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.updateCanvasSize)
    if (this.stompClient) {
      this.stompClient.disconnect()
    }
  },
  methods: {
    toggleShapesMenu () {
      this.showShapesMenu = !this.showShapesMenu
    },
    setShape (shape) {
      this.currentShape = shape
      this.drawMode = 'shape_' + shape
      this.showShapesMenu = false
    },
    setDrawMode (mode) {
      this.drawMode = mode
    },
    getCoordinates (e) {
      if (e.touches) {
        const rect = this.$refs.canvas.getBoundingClientRect()
        return {
          x: e.touches[0].clientX - rect.left,
          y: e.touches[0].clientY - rect.top
        }
      } else {
        return { x: e.offsetX, y: e.offsetY }
      }
    },
    updateCanvasSize () {
      const canvas = this.$refs.canvas
      if (canvas) {
        canvas.width = this.$el.clientWidth
        canvas.height = this.$el.clientHeight
        this.initCanvas()
      }
    },
    setCanvasBackground (imgSrc) {
      const canvas = this.$refs.canvas
      if (!canvas) return
      this.initCanvas()
      const context = canvas.getContext('2d')
      const image = new Image()
      image.onload = () => {
        context.clearRect(0, 0, canvas.width, canvas.height)
        context.drawImage(image, 0, 0, canvas.width, canvas.height)
      }
      image.src = imgSrc
    },
    initCanvas () {
      const canvas = this.$refs.canvas
      if (canvas) {
        this.context = canvas.getContext('2d')
        this.context.strokeStyle = this.selectedColor
        this.context.lineWidth = 5
        this.context.lineCap = 'round'
        this.context.lineJoin = 'round'
      }
    },
    updateClearRect () {
      this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
    },
    startDrawing (e) {
      if (!this.canDraw) return
      this.drawing = true
      /*
      this.lastX = e.offsetX
      this.lastY = e.offsetY
      this.context.beginPath()
      if (this.drawMode === 'line') {
        this.context.moveTo(this.lastX, this.lastY)
      }
      */
      const { x, y } = this.getCoordinates(e)
      this.lastX = x
      this.lastY = y
      this.context.beginPath()
      if (this.drawMode === 'line') {
        this.context.moveTo(this.lastX, this.lastY)
      }
    },
    draw (e) {
      if (!this.canDraw || !this.drawing) return
      const { x, y } = this.getCoordinates(e)
      if (this.drawMode === 'free') {
        this.drawFree(x, y)
      } else if (this.drawMode === 'line') {
        this.drawLine(x, y)
      }
    },
    drawFree (x, y) {
      requestAnimationFrame(() => {
        this.context.lineTo(x, y)
        this.context.stroke()
        this.context.beginPath()
        this.context.moveTo(x, y)
      })
      /*
      this.context.lineTo(x, y)
      this.context.stroke()
      this.lastX = x
      this.lastY = y
      */
    },
    drawLine (x, y) {
      requestAnimationFrame(() => {
        this.updateClearRect()
        this.initCanvas()
        this.context.beginPath()
        this.context.moveTo(this.lastX, this.lastY)
        this.context.lineTo(x, y)
        this.context.stroke()
        this.context.closePath()
      })
      /*
      this.updateClearRect()
      this.context.beginPath()
      this.context.moveTo(this.lastX, this.lastY)
      this.context.lineTo(x, y)
      this.context.stroke()
      */
    },
    drawShape (startX, startY, endX, endY) {
      this.context.strokeStyle = this.selectedColor || 'black'
      const centerX = (startX + endX) / 2
      const centerY = (startY + endY) / 2
      const distanceX = endX - startX
      const distanceY = endY - startY
      switch (this.currentShape) {
        case 'circle':
          this.context.ellipse(centerX, centerY, Math.abs(distanceX) / 2, Math.abs(distanceY) / 2, 0, 0, Math.PI * 2)
          break
        case 'square':
          this.context.rect(startX, startY, distanceX, distanceY)
          break
        case 'triangle':
          this.context.moveTo(startX, startY)
          this.context.lineTo(endX, endY)
          this.context.lineTo(startX * 2 - endX, endY)
          this.context.closePath()
          break
      }
      this.context.stroke()
    },
    stopDrawing (e) {
      if (!this.drawing) return
      if (this.drawMode === 'line') {
        this.context.lineTo(e.offsetX, e.offsetY)
        this.context.stroke()
        this.context.closePath()
      } else if (this.drawMode.startsWith('shape_') && this.currentShape) {
        this.drawShape(this.lastX, this.lastY, e.offsetX, e.offsetY)
      }
      this.drawing = false
    },
    closeCanvas () {
      this.drawing = false
      this.$emit('toggle-canvas')
      if (this.stompClient) {
        this.stompClient.disconnect()
      }
    },
    removeAll () {
      this.context.clearRect(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
    },
    sendCanvas () {
      const canvas = this.$refs.canvas
      if (!canvas) return
      // 이미지 URL 생성
      const imageUrl = canvas.toDataURL('image/png')
      // 다운로드 링크 생성
      const link = document.createElement('a')
      link.download = 'canvas-image.png' // 다운로드될 파일명
      link.href = imageUrl
      // link.click() // 링크 클릭 시 다운로드
      this.$emit('canvas-prepared', { imageUrl })
    },
    triggerFileInput () {
      this.$refs.fileInput.click()
    },
    handleFile (e) {
      const file = e.target.files[0]
      if (file && file.type.startsWith('image/')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          this.setCanvasBackgroundSetting(e.target.result)
        }
        reader.readAsDataURL(file)
      }
    },
    setCanvasBackgroundSetting (imgSrc) {
      const canvas = this.$refs.canvas
      if (!canvas) return
      const context = canvas.getContext('2d')
      const image = new Image()
      image.onload = () => {
        context.clearRect(0, 0, canvas.width, canvas.height)
        context.drawImage(image, 0, 0, canvas.width, canvas.height)
      }
      image.src = imgSrc
    },
    shareDrawing () {
      alert('준비중입니다.')
      /*
      this.initializeSocketConnection()
      const canvas = this.$refs.canvas
      canvas.toBlob(blob => {
        const reader = new FileReader()
        reader.onload = () => {
          const arrayBuffer = reader.result
          const compressedData = pako.gzip(new Uint8Array(arrayBuffer))
          const blobToSend = new Blob([compressedData], {type: 'application/octet-stream'})
          if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send(`/chat/send/shareDrawing/${this.roomId}`, {}, blobToSend)
          }
        }
        reader.readAsArrayBuffer(blob)
      }, 'image/jpeg', 0.75)
      */
    },
    receiveCanvas (imageUrl) {
      if (imageUrl === '{}') {
        alert('처리중입니다')
        return
      }
      const image = new Image()
      image.onload = () => {
        this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
        this.context.drawImage(image, 0, 0, this.canvas.width, this.canvas.height)
      }
      image.src = imageUrl
    },
    initializeSocketConnection () {
      const socket = new SockJS(`${this.apiDrawUrl}:${this.apiDrawPort}/ws-stomp`)
      this.stompClient = Stomp.over(socket)
      this.stompClient.debug = null

      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe(`/chat/send/draw/${this.roomId}`, message => {
          this.receiveCanvas(message.body)
        })
      }, error => {
        console.error('STOMP connection error:', error)
      })
    }
  },
  watch: {
    selectedColor (newColor) {
      this.context.strokeStyle = newColor
    }
  }

}
</script>

<style scoped>
#canvasArea {
  background-image: radial-gradient(circle at center, black 1px, transparent 1px), radial-gradient(circle at center, black 1px, transparent 1px);
  background-size: 50px 50px;
  background-position: 0 0, 50px 50px;
  border: 1px solid rgba(0, 0, 0, .4);
  box-sizing: border-box;
  position: absolute;
  left: 8px;
  top: 8px;
  bottom: 8px;
  right: 8px;
  width: calc(100% - 16px);
  height: calc(100% - 16px);
  z-index: 10;
}

#canvasArea > div {
  background-color: rgba(255, 255, 255, 0.8);
  cursor: crosshair;
  width: 100%;
  height: 100%;
}

.bottom {
    bottom: 0;
}

.top {
    top: 0;
}

.left {
    left: 0;
}

.right {
    right: 0;
}

.controlBtn {
    position: absolute;
}

.controlBtn li {
    display: inline-block;
    margin:1vh 0.5vw;
}

.controlBtn li > ul {
  bottom: 40px;
}

.controlBtn li > ul > li {
  display: block;
  margin: 20px 0;
  min-height: 28px;
}

.controlBtn button {
    padding: 5px 10px;
    border:1px solid #000;
    box-sizing: border-box;
    text-align: center;
    min-width: 80px;
    background-color: #fff;
}

input[type="color"] {
  border: none;
  border-radius: 50%;
  height: 30px;
  width: 30px;
  -webkit-appearance: none;
  appearance: none;
  padding: 0;
  cursor: pointer;
}

input[type="color"]::-webkit-color-swatch-wrapper {
  border-radius: 50%;
  padding: 0;
}

input[type="color"]::-webkit-color-swatch {
  border: none;
  border-radius: 50%;
}

.controlBtn button.active, .controlBtn button:hover {
  background-color: #cc8f8f; /* 호버 및 활성 상태의 배경색 */
  color: #333; /* 호버 및 활성 상태의 텍스트 색상 */
  border-color: #666; /* 호버 및 활성 상태의 테두리 색상 */
}
</style>
