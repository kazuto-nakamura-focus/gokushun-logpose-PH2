<template>
  <v-app>
    <MSEditDevice
      v-if="prepared"
      :mode="mode"
      :onEnd="handleBack"
      :useFieldInfoDataList="useFieldInfoDataList"
      :useDeviceInfoData="useDeviceInfoData"
      :useDeviceMasters="useDeviceMasters"
    />
  </v-app>
</template>

<script>
import { useFieldList } from "@/api/ManagementScreenTop/MSField";
import {
  useDeviceInfo,
  useDeviceMastersAPI,
} from "@/api/ManagementScreenTop/MSDevice";
import MSEditDevice from "./MSEditDevice.vue";

export default {
  props: {
    onEnd: {
      type: Function,
      required: true,
    },
    selectedDevice: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      mode: "update",
      prepared: false,
      useFieldInfoDataList: [],
      useDeviceInfoData: null,
      useDeviceMasters: {
        sensorContents: {},
        sensorModels: {},
        sensorSizes: {},
      },

      PRIVATE: {
        convertMap(list, map) {
          for (const item of list) {
            map[item.id] = item.name;
          }
        },
        convertMaster(masterlist, masterMap) {
          this.convertMap(masterlist.sensorContents, masterMap.sensorContents);
          this.convertMap(masterlist.sensorModels, masterMap.sensorModels);
          this.convertMap(masterlist.sensorSizes, masterMap.sensorSizes);
        },
      },
    };
  },

  components: {
    MSEditDevice,
  },

  mounted() {
    this.initialize();
  },

  methods: {
    initialize: function () {
      // 表示モードの設定
      this.mode = null == this.selectedDevice.id ? "add" : "update";

      var preparedCount = 0;
      // 圃場一覧取得
      useFieldList()
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if(status === 0){
            this.useFieldInfoDataList = data;
            preparedCount++;
            if (preparedCount == 3) this.prepared = true;
          }else{
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
      //マスターデータ取得
      useDeviceMastersAPI()
        .then((response) => {
          const { status, message, data} = response["data"];
          if(status === 0){
            this.PRIVATE.convertMaster(data, this.useDeviceMasters);
            preparedCount++;
            if (preparedCount == 3) this.prepared = true;
          }else{
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });

      if(null != this.selectedDevice.id) {
      //デバイス情報詳細取得(API)
      useDeviceInfo(this.selectedDevice.id)
        .then((response) => {
          const { status, message, data } = response["data"];
          if(status === 0){
            this.useDeviceInfoData = data;
            preparedCount++;
            if (preparedCount == 3) this.prepared = true;
          }else{
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
      }else {
        preparedCount++;
          if (preparedCount == 3) this.prepared = true;
      }
    },

    //* 下位からの返却
    handleBack: function (isChanged) {
      this.onEnd(isChanged);
    },
  },
};
</script>
