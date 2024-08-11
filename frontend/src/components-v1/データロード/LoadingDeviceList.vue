<template>
  <v-app>
    <v-card>
      <v-card-title>センサーロード状態 [{{ currentTime }} 現在]</v-card-title>

      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="items"
          dense
          elevation-6
          style="font-size: 9pt"
        >
          <template v-slot:[`item.loadTime`]="{ item }">
            <div v-if="item.loadTime != null">
              <a
                href="javascript:void(0)"
                @click="openLog(item.name, item.id, 1)"
                >{{ item.loadTime }}</a
              >
            </div>
            <div v-else style="text-align: center">-</div>
          </template>
          <template v-slot:[`item.updateTime`]="{ item }">
            <div v-if="item.updateTime != null">
              <a
                href="javascript:void(0)"
                @click="openLog(item.name, item.id, 2)"
                >{{ item.updateTime }}</a
              >
            </div>
            <div v-else style="text-align: center">-</div>
          </template>
        </v-data-table>
      </v-card-text>

      <v-card-actions class="justify-center">
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="initialize()"
          >表の更新</v-btn
        >
      </v-card-actions>
    </v-card>
    <log-dialog ref="rLogDialog"></log-dialog>
  </v-app>
</template>
  
<script>
import { useGetAllDataLoadStatus } from "@/api/ManagementScreenTop/MSDevice";
import moment from "moment";
import LogDialog from "@/components-v1/データロード/ログダイアログ.vue";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true, width: "20%" },
  { text: "ID", value: "id", sortable: true, width: "5%" },
  { text: "ステータス", value: "status", sortable: true, width: "20%" },
  {
    text: "最後の全センサーロード",
    value: "loadTime",
    sortable: true,
    width: "20%",
  },
  {
    text: "最後の更新バッチ",
    value: "updateTime",
    sortable: true,
    width: "20%",
  },
  {
    text: "データ状態",
    value: "dataStatus",
    sortable: true,
    width: "15%",
  },
];

export default {
  data() {
    return {
      headers: HEADERS,
      items: [],
      currentTime: moment().format("YYYY-MM-DD HH:mm:SS"),
    };
  },
  components: {
    LogDialog,
  },
  mounted() {
    this.initialize();
  },
  methods: {
    // ======================================================
    // ログデータ情報を表示する
    // ======================================================
    initialize: function () {
      useGetAllDataLoadStatus()
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          this.items = data;
        })
        .catch((error) => {
          //失敗時
          alert("デバイスデータの取得に失敗しました。");
          console.log(error);
          this.isRunning = false;
        });
    },
    // ======================================================
    // 表示設定
    // ======================================================
    openLog: function (name, id, type) {
      this.$refs.rLogDialog.isDialog = true;
      this.$nextTick(function () {
        this.$refs.rLogDialog.initialize(name, id, type);
      });
    },
  },
};
</script>
<style lang="scss">
</style>
  