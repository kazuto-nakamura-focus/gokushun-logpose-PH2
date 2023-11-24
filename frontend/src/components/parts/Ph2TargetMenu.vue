<template>
  <v-container class="spacing-playground pa-5">
    <div v-show="model" style="margin: 0; padding: 0">
      <ButtonSelector
        :titleWidth="100"
        :title="menuTitle"
        :data="menuModel"
        :handleClick="handleClickModel"
        :multiple="false"
      />
      <v-row><v-divider /></v-row>
    </div>

    <v-expand-transition>
      <div>
        <ButtonSelector
          :titleWidth="100"
          :title="grouthTitle"
          :data="fieldList"
          :handleClick="handleClickGrouth"
          :multiple="false"
        />
        <v-row><v-divider /></v-row>
      </div>
    </v-expand-transition>

    <v-expand-transition>
      <div v-show="selectedField != null">
        <ButtonSelector
          :titleWidth="100"
          :title="deviceTitle"
          :data="deviceList"
          :handleClick="handleClickDevice"
          :multiple="false"
        />
        <v-row><v-divider /></v-row>
      </div>
    </v-expand-transition>
    <div v-show="model" style="margin: 0; padding: 0">
      <v-expand-transition>
        <div v-show="selectedDevice != null">
          <ButtonSelector
            :titleWidth="100"
            :title="yearTitle"
            :data="yearList"
            :handleClick="handleClickYear"
            :multiple="false"
          />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>
    </div>
  </v-container>
</template>
  
  <script>
import ButtonSelector from "./Ph2ButtonSelector";
import { useModels } from "@/api/Top";

export default {
  props: {
    shared /** MountController */: { required: true },
    model: { required: true },
  },
  data() {
    return {
      // 表示タイプ
      isModelType: this.model,
      // ルートデータ
      menuModel: [],
      targets: [],
      selectedMap: new Map(),

      // タイトル
      menuTitle: "モデル",
      grouthTitle: "圃場",
      yearTitle: "年度",
      deviceTitle: "デバイス",

      // 表示リスト
      selectedModels: {},
      fieldList: [],
      deviceList: [],
      yearList: [],

      // 選択データ
      selectedModel: null,
      selectedField: null,
      selectedDevice: null,
      selectedYear: null,

      //* ==============================================
      //* 内部処理関数群
      //* ==============================================
      PRIVATE: {
        //* --------------------------------------------
        //* 選択結果を返す
        //* --------------------------------------------
        getAllSelected(data) {
          return {
            selectedModel: data.selectedModel,
            selectedField: data.selectedField,
            selectedDevice: data.selectedDevice,
            selectedYear: data.selectedYear,
          };
        },
        //* --------------------------------------------
        //* 選択状態を返却する
        //* --------------------------------------------
        isAllSelected(data) {
          return (
            (data.selectedModel != null || !data.isModelType) &&
            data.selectedField != null &&
            data.selectedDevice != null &&
            (data.selectedYear != null || !data.isModelType)
          );
        },
        //* --------------------------------------------
        //* リストの作成と内既に選択されているものがあればそれを返す
        //* --------------------------------------------
        getSelected(map, list) {
          list.length = 0;
          let selected = null;
          for (const item of map.values()) {
            list.push(item);
            if (item.active) selected = item;
          }
          return selected;
        },
        //* --------------------------------------------
        //* マウント要求側に通知
        //* --------------------------------------------
        send(data) {
          data.shared.onConclude(
            this.isAllSelected(data),
            this.getAllSelected(data)
          );
        },
      },
    };
  },
  components: {
    ButtonSelector,
  },
  mounted() {
    this.shared.mount(this);
  },
  methods: {
    initialize() {
      useModels(this.isModelType)
        .then((response) => {
          //成功時
          const results = response["data"].data;
          var models = results.models;
          //* モデルコンポーネントの作成
          for (const item of models) {
            //* モデルボタンの作成
            var label = {
              name: item.name,
              id: item.id,
              active: false,
            };
            this.menuModel.push(label);
          }
          this.targets = results.targets;
          for (const data of this.targets) {
            let field = this.selectedMap.get(data.fieldId);
            if (null == field) {
              field = {
                id: data.fieldId,
                name: data.field,
                active: false,
                deviceMap: new Map(),
              };
              this.selectedMap.set(data.fieldId, field);
            }
            let device = field.deviceMap[data.deviceId];
            if (null == device) {
              device = {
                id: data.deviceId,
                name: data.device,
                active: false,
                yearMap: new Map(),
              };
              field.deviceMap.set(data.deviceId, device);
            }
            let year = device.yearMap[data.year];
            if (null == year) {
              year = {
                id: data.year,
                name: data.year,
                startDate:data.startDate,
                active: false,
              };
              device.yearMap.set(data.year, year);
            }
          } //* 圃場リストへの処理
          if (this.fieldList.length == 0) {
            for (let element of this.selectedMap.values()) {
              this.fieldList.push(element);
            }
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //* --------------------------------------------
    //* モデルを選択した時の処理
    //* --------------------------------------------
    handleClickModel: function (item) {
      //* 選択済みの場合
      this.selectedModel = item;
      this.PRIVATE.send(this);
    },
    //* --------------------------------------------
    //* 圃場を選択した時の処理
    //* --------------------------------------------
    handleClickGrouth: function (item, index, active) {
      //* 選択時
      if (active) {
        this.selectedField = item;
        this.selectedDevice = this.PRIVATE.getSelected(
          item.deviceMap,
          this.deviceList
        );
        if (null != this.selectedDevice) {
          this.selectedYear = this.PRIVATE.getSelected(
            this.selectedDevice.yearMap,
            this.yearList
          );
        }
        //* 選択解除時
      } else {
        this.selectedField = null;
        this.deviceList.length = 0;
        this.selectedDevice = null;
        this.yearList.length = 0;
        this.selectedYear = null;
      }
      this.PRIVATE.send(this);
    },
    //* --------------------------------------------
    //* デバイスを選択した時の処理
    //* --------------------------------------------
    handleClickDevice: function (item, index, active) {
      //* 選択時
      if (active) {
        this.selectedDevice = item;
        if (null != this.selectedDevice) {
          this.selectedYear = this.PRIVATE.getSelected(
            this.selectedDevice.yearMap,
            this.yearList
          );
        }
        //* 選択解除時
      } else {
        this.selectedDevice = null;
        this.yearList.length = 0;
        this.selectedYear = null;
      }
      this.PRIVATE.send(this);
    },
    //* --------------------------------------------
    //* 年度を選択した時の処理
    //* --------------------------------------------
    handleClickYear: function (item, index, active) {
      active == true ? (this.selectedYear = item) : (this.selectedYear = null);
      if (active) {
        // 選択をSTORE
        //* 圃場
        this.$store.dispatch("changeSelectedField", this.selectedField);
        //* デバイス名
        this.$store.dispatch("changeSelectedDevice", this.selectedDevice);
        //* 年度
        this.$store.dispatch("changeSelectedYear", this.selectedYear);
      }
      this.PRIVATE.send(this);
    },
  },
};
</script>
  
  <style lang="scss">
// ヘッダー部
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: 90%;
  flex-wrap: wrap;
}

.menu {
  width: 90%;
  display: flex;
  justify-content: space-strech;
  flex-wrap: wrap;
}

.date {
  //display: flex;
  font-size: 11pt;
  // width: 80%;
  //justify-content: space-strech;
  //flex-wrap: wrap;
}
</style>
  