<template>
  <div id="draw">
    <canvas ref="canvas" width="800" height="800" @mousedown="startDrawing" @mousemove="draw" @mouseup="stopDrawing" @mouseout="stopDrawing"></canvas>
    <div id=designtool>
      <button @click="buttonErase">모두 지우기</button>
      <button @click="increaseThickness">두껍게</button>
      <button @click="decreaseThickness">얇게</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Draw',
  data () {
    return {
      drawing: false,
      context: null,
      lastX: 0,
      lastY: 0
    }
  },
  mounted () {
    this.initCanvas()
  },
  methods: {
    initCanvas () {
      const canvas = this.$refs.canvas
      this.context = canvas.getContext('2d')
      this.context.strokeStyle = '#000'
      this.context.lineWidth = 5
    },
    startDrawing (e) {
      this.drawing = true
      this.lastX = e.offsetX
      this.lastY = e.offsetY
    },
    draw (e) {
      if (!this.drawing) return
      this.context.beginPath()
      this.context.moveTo(this.lastX, this.lastY)
      this.context.lineTo(e.offsetX, e.offsetY)
      this.context.stroke()
      this.lastX = e.offsetX
      this.lastY = e.offsetY
    },
    stopDrawing () {
      this.drawing = false
    },
    buttonErase () {
      this.context.clearRect(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
    },
    increaseThickness () {
      const canvas = this.$refs.canvas
      this.context = canvas.getContext('2d')
      this.context.lineWidth += 1
    },
    decreaseThickness () {
      const canvas = this.$refs.canvas
      this.context = canvas.getContext('2d')
      if (this.context.lineWidth > 1) {
        this.context.lineWidth -= 1
      }
    }
  }
}
</script>

<style scoped>
  header {
    line-height: 1.5;
  }

  @media (min-width: 1024px) {
    header {
      display: flex;
      place-items: center;
      padding-right: calc(var(--section-gap) / 2);
    }
    header .wrapper {
      display: flex;
      place-items: flex-start;
      flex-wrap: wrap;
    }
  }

  canvas {
    border: 2px solid white;
    background-color: green;
    cursor: pointer;
  }

  bottom {
    width: 200px;
    height: 50px;
  }
</style>
