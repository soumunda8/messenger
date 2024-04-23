<template>
    <div id="canvasArea" ref="canvasArea">
        <canvas ref="canvas" @mousedown="startDrawing" @mousemove="draw" @mouseup="stopDrawing" @mouseleave="stopDrawing"></canvas>
        <ul class="controlBtn bottom left">
            <li><button :class="{ active: drawMode === 'line' }" @click="setDrawMode('line')">선</button></li>
            <li>
              <button @click="toggleShapesMenu">도형</button>
              <ul v-show="showShapesMenu" style="display: none;position: absolute;padding-left: 0;">
                <li><button :class="{ active: drawMode === 'circle' }" @click="setShape('circle')">○</button></li>
                <li><button :class="{ active: drawMode === 'triangle' }" @click="setShape('triangle')">△</button></li>
                <li><button :class="{ active: drawMode === 'square' }" @click="setShape('square')">□</button></li>
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
    </div>
</template>

<script>
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
      currentShape: null
    }
  },
  mounted () {
    this.updateCanvasSize()
    window.addEventListener('resize', this.updateCanvasSize)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.updateCanvasSize)
  },
  methods: {
    toggleShapesMenu () {
      alert('준비중입니다.')
      // this.showShapesMenu = !this.showShapesMenu
    },
    setShape (shape) {
      this.currentShape = shape
      this.drawMode = 'shape'
    },
    setDrawMode (mode) {
      this.drawMode = mode
    },
    updateCanvasSize () {
      const canvas = this.$refs.canvas
      if (canvas) {
        canvas.width = this.$el.clientWidth
        canvas.height = this.$el.clientHeight
        this.initCanvas()
      }
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
    startDrawing (e) {
      this.drawing = true
      this.lastX = e.offsetX
      this.lastY = e.offsetY
      this.context.beginPath()
      if (this.drawMode === 'line') {
        this.context.moveTo(this.lastX, this.lastY)
      }
    },
    draw (e) {
      if (!this.drawing) return
      if (this.drawMode === 'free') {
        this.context.lineTo(e.offsetX, e.offsetY)
        this.context.stroke()
        this.context.beginPath()
        this.context.moveTo(e.offsetX, e.offsetY)
      } else if (this.drawMode === 'line') {
        this.updateLinePreview(e.offsetX, e.offsetY)
      } else if (this.drawMode === 'shape' && this.currentShape) {
        this.drawShape(e.offsetX, e.offsetY)
      }
    },
    drawShape (x, y) {
      const size = 50
      this.context.beginPath()
      switch (this.currentShape) {
        case 'circle':
          this.context.arc(x, y, size, 0, 2 * Math.PI)
          break
        case 'triangle':
          this.context.moveTo(x, y - size)
          this.context.lineTo(x + size, y + size)
          this.context.lineTo(x - size, y + size)
          this.context.closePath()
          break
        case 'square':
          this.context.rect(x - size / 2, y - size / 2, size, size)
          break
      }
      this.context.fill()
    },
    stopDrawing (e) {
      if (!this.drawing) return
      if (this.drawMode === 'line') {
        this.context.lineTo(e.offsetX, e.offsetY)
        this.context.stroke()
        this.context.closePath()
      }
      this.drawing = false
    },
    updateLinePreview (x, y) {
      requestAnimationFrame(() => {
        this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
        this.initCanvas()
        this.context.beginPath()
        this.context.moveTo(this.lastX, this.lastY)
        this.context.lineTo(x, y)
        this.context.stroke()
        this.context.closePath()
      })
    },
    closeCanvas () {
      this.drawing = false
      this.$emit('toggle-canvas')
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

canvas {
  background-color: rgba(255, 255, 255, 0.8);
  cursor: crosshair;
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
  background-color: #fff;
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
