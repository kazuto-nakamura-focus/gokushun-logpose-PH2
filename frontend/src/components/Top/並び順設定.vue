<template>
  <div style="background-color: white">
    <v-container class="spacing-playground pa-5" dense background-color="white">
      <v-row>
        <v-col cols="6">
          <h3>表示リスト</h3>
          <h5>ドラッグ＆ドロップで表示順位を変更してください。</h5>
          <draggable
            v-model="showList"
            class="list-group"
            ghost-class="ghost"
            chosen-class="chosen"
            @start="dragging = true"
            @end="dragging = false"
            group="devices"
          >
            <div
              style="
                margin: 0px 10px;
                padding: 5px;
                border: 1px solid #999;
                width: 90%;
                font-size: 9pt;
              "
              class="list-group-item item"
              v-for="item in showList"
              :key="item.deviceId"
            >
              {{ item.title }}-{{ item.comment }}
            </div>
          </draggable>
        </v-col>
        <v-col cols="6">
          <h3>非表示リスト</h3>
          <h5>非表示にしたいデバイスをここにドロップしてください。</h5>
          <draggable
            v-model="hideList"
            class="list-group"
            ghost-class="ghost"
            chosen-class="chosen"
            @start="dragging = true"
            @end="dragging = false"
            group="devices"
          >
            <div
              style="
                margin: 0px 10px;
                padding: 5px;
                border: 1px solid #999;
                width: 90%;
                font-size: 9pt;
              "
              class="list-group-item item"
              v-for="item in hideList"
              :key="item.deviceId"
            >
              {{ item.title }}-{{ item.comment }}
            </div>
          </draggable>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <div style="width: 100%; text-align: center">
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
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
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
  mounted() {},
  methods: {
    initialize() {
      console.log("showList", this.showList);
      console.log("hideList", this.hideList);
    },
    saveData() {
      localStorage.setItem("showList", JSON.stringify(this.showList));
      this.$emit("save", this.showList);
    },
    close() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped>
.list-group {
  min-height: 50px;
}

.ghost {
  opacity: 0.4;
}

.chosen {
  background-color: #ffeb3b;
}
</style>