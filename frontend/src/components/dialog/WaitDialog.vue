<template>
  <v-dialog v-model="isDialog" width="33%" persistent>
    <v-card>
      <v-card-title class="dialog-warning-header justify-center">
        <div style="text-align: center">
          {{ message }}
        </div>
      </v-card-title>
      <v-card-title class="dialog-warning-header justify-center">
        <div style="text-align: center">{{ interval.toFixed(2) }}</div>
      </v-card-title>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  data() {
    return {
      isDialog: false,
      message: "",
      startTime: 0,
      timer: 0, // setInterval()の格納用
      interval: 0, // 計測時間
      accum: 0, // 累積時間(stopしたとき用)
    };
  },
  // **********************************************************
  // 関数
  // **********************************************************
  methods: {
    // ======================================================
    // ダイアログのオープンとメッセージの表示
    // ======================================================
    start(message) {
      this.isDialog = true;
      this.message = message;
      this.startTime = Date.now();
      setInterval(() => {
        this.interval = this.accum + (Date.now() - this.startTime) * 0.001;
      }, 10);
    },
    // ======================================================
    // ダイアログの終了
    // ======================================================
    finish() {
      this.isDialog = false;
    },
  },
};
</script>