<template>
  <v-card>
    <v-card-title>表示リスト</v-card-title>
    <draggable
      v-model="showList"
      :list="showList"
      class="list-group"
      ghost-class="ghost"
      @start="dragging = true"
      @end="dragging = false"
    >
      <div
        class="list-group-item"
        v-for="element in showList"
        :key="element.deviceId"
      >
        {{ element.title }}
      </div>
    </draggable>

    <v-card-title>非表示リスト</v-card-title>
    <draggable
      v-model="hideList"
      class="list-group"
      ghost-class="ghost"
      @start="dragging = true"
      @end="dragging = false"
    >
      <div
        class="list-group-item"
        v-for="element in hideList"
        :key="element.deviceId"
      >
        {{ element.title }}
      </div>
    </draggable>

    <div>
      <v-btn
        color="primary"
        class="ma-2 white--text"
        elevation="2"
        @click="saveData()"
        >更新</v-btn
      >
      <v-btn
        color="primary"
        class="ma-2 white--text"
        elevation="2"
        @click="close()"
        >キャンセル</v-btn
      >
    </div>
  </v-card>
</template>
  
<script>
import draggable from "vuedraggable";

export default {
  props: {
    displayData: { required: true },
    unDisplayData: { required: true },
  },
  components: { draggable },
  data() {
    return {
      showList: this.displayData,
      hideList: this.unDisplayData,
    };
  },
  methods: {
    initialize() {
      console.log("showList", this.showList);
      console.log("hideList", this.hideList);
    },
    saveData() {
      localStorage.setItem("dashboardList", this.showList);
      this.$emit("save", this.showList);
    },
    close() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped>
.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}
</style>