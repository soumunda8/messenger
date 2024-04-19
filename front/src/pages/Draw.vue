<template>
<div id="draw">
    <div class="app-wrapper">
      <!--
      <canvas id="canvas"></canvas>
      <div class="cursor" id="cursor"></div>
      -->
      <canvas id="canvas" @mousedown="handleMouseDown" @mouseup="handleMouseUp" @mouseleave="handleMouseLeave" @mousemove="handleMouseMove"></canvas>
      <div id="cursor" style="position: absolute; width: 20px; height: 20px; background: black; border-radius: 10px;"></div>
      <div class="controls">
        <div class="btn-row">
          <div class="history" title="history">{{ history.length }}</div>
        </div>
        <div class="btn-row">
          <button type="button" @click="removeHistoryItem" :class="{ disabled: !history.length }" title="Undo">
            <i class="ion ion-reply"></i>
          </button>
          <button type="button" @click="removeAllHistory" :class="{ disabled: !history.length }" title="Clear all">
            <i class="ion ion-trash-a"></i>
          </button>
        </div>
        <div class="btn-row">
          <button title="Brush options" @click="popups.showOptions = !popups.showOptions">
            <i class="ion ion-android-create"></i>
          </button>
          <div class="popup" v-if="popups.showOptions">
            <div class="popup-title">Options</div>
            <button title="Restrict movement vertical" @click="options.restrictY = !options.restrictY; options.restrictX = false" :class="{ active: options.restrictY }">
              <i class="ion ion-arrow-right-c"></i>Restrict vertical
            </button>
            <button title="Restrict movement horizontal" @click="options.restrictX = !options.restrictX; options.restrictY = false" :class="{ active: options.restrictX }">
              <i class="ion ion-arrow-up-c"></i>Restrict horizontal
            </button>
            <button type="button" @click="simplify" :class="{ disabled: !history.length }" title="Simplify paths">
              <i class="ion ion-wand"></i>Simplify paths
            </button>
            <button type="button" @click="jumble" :class="{ disabled: !history.length }" title="Go nutz">
              <i class="ion ion-shuffle"></i>Go nutz
            </button>
          </div>
        </div>

        <div class="btn-row">
          <button title="Pick a brush size" @click="popups.showSize = !popups.showSize" :class="{ active: popups.showSize }">
            <i class="ion ion-android-radio-button-on"></i>
            <span class="size-icon">{{ size }}</span>
          </button>
          <div class="popup" v-if="popups.showSize">
            <div class="popup-title">Brush size</div>
            <label v-for="(sizeItem, index) in sizes" :key="index" class="size-item">
              <input type="radio" name="size" v-model="size" :value="sizeItem">
              <span class="size" :style="{width: sizeItem + 'px', height: sizeItem + 'px'}" @click="popups.showSize = !popups.showSize"></span>
            </label>
          </div>
        </div>

        <div class="btn-row">
          <button title="Pick a color" @click="popups.showColor = !popups.showColor" :class="{ active: popups.showColor }">
            <i class="ion ion-android-color-palette"></i>
            <span class="color-icon" :style="{backgroundColor: color}"></span>
          </button>
          <div class="popup" v-if="popups.showColor">
            <div class="popup-title">Brush color</div>
            <label v-for="colorItem in colors" :key="colorItem" class="color-item">
              <input type="radio" name="color" v-model="color" :value="colorItem">
              <span :class="'color color-' + colorItem" :style="{backgroundColor: colorItem}" @click="popups.showColor = !popups.showColor"></span>
            </label>
          </div>
        </div>

        <div class="btn-row">
          <button title="Save" @click="popups.showSave = !popups.showSave">
            <i class="ion ion-android-cloud-outline"></i>
          </button>
          <div class="popup" v-if="popups.showSave">
            <div class="popup-title">Save your design</div>
            <div class="form">
              <input type="text" placeholder="Save name" v-model="save.name" />
              <div v-if="save.name.length &lt; 3" class="text-faded">
                <i>Min 3 characters</i>
              </div>
              <span class="btn" @click="saveItem">Save as
                <span class="text-faded">{{ save.name }}</span>
              </span>
            </div>
            <div class="saves" v-if="save.saveItems.length">
              <div class="popup-title">Load a save</div>
              <div class="save-item" v-for="(item, index) in save.saveItems" :key="index">
                <h3>{{ item.name }}</h3>
                <span class="btn" @click="loadSave(item)">load</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Draw',
  data () {
    return {
      app: {
        history: [],
        color: '#13c5f7',
        popups: {
          showColor: false,
          showSize: false,
          showWelcome: true,
          showSave: false,
          showOptions: false
        },
        options: {
          restrictY: false,
          restrictX: false
        },
        save: {
          name: '',
          saveItems: []
        },
        size: 12,
        colors: [
          '#d4f713', '#13f7ab', '#13f3f7', '#13c5f7', '#138cf7',
          '#1353f7', '#2d13f7', '#7513f7', '#a713f7', '#d413f7',
          '#f713e0', '#f71397', '#f7135b', '#f71313', '#f76213',
          '#f79413', '#f7e013'
        ],
        sizes: [6, 12, 24, 48],
        weights: [2, 4, 6]
      },
      draw: null
    }
  },
  methods: {
    removeHistoryItem () {
      this.app.history.splice(this.app.history.length - 2, 1)
      this.draw.redraw()
    },
    removeAllHistory () {
      this.app.history = []
      this.draw.redraw()
    },
    simplify () {
      let simpleHistory = []
      this.app.history.forEach((item, i) => {
        if (i % 6 !== 1 || item.isDummy) {
          simpleHistory.push(item)
        }
      })
      this.app.history = simpleHistory
      this.draw.redraw()
    },
    jumble () {
      this.app.history.forEach((item, i) => {
        item.r += Math.sin(i * 20) * 5
      })
      this.app.history = this.shuffle(this.app.history)
      this.draw.redraw()
    },
    shuffle (a) {
      let b = []
      a.forEach((item, i) => {
        if (!item.isDummy) {
          let l = b.length
          let r = Math.floor(l * Math.random())
          b.splice(r, 0, item)
        }
      })
      for (let i = 0; i < b.length; i++) {
        if (i % 20 === 1) {
          b.push(this.draw.getDummyItem())
        }
      }
      return b
    },
    saveItem () {
      if (this.app.save.name.length > 2) {
        let historyItem = {
          history: this.app.history.slice(),
          name: this.app.save.name
        }
        this.app.save.saveItems.push(historyItem)
        this.app.save.name = ''
      }
    },
    loadSave (item) {
      this.app.history = item.history.slice()
      this.draw.redraw()
    },
    initializeDraw () {
      this.draw = new Draw(this)
    }
  },
  mounted () {
    this.initializeDraw()
  }
}

class Draw {
  constructor (vueInstance) {
    this.vueInstance = vueInstance
    this.c = document.getElementById('canvas')
    this.ctx = this.c.getContext('2d')
    this.mouseDown = false
    this.mouseX = 0
    this.mouseY = 0
    this.setSize()
    this.listen()
    this.redraw()
  }
  // Methods for Draw class here (such as setSize, listen, etc.)
}

</script>

<style scoped>
  /* Define component-specific styles here */
</style>
