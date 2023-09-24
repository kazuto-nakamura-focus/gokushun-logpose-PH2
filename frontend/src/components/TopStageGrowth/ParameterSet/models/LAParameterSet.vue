<template>
  <v-container>
    <div>
      <div class="text-subtitle-1" v-if="!isEditMode">パラメータ</div>
      <div class="text-subtitle-1" v-if="isEditMode">編集前パラメータ</div>

      <v-container class="leafArea">
        <v-row>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">樹冠葉面積モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">葉枚数モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">a</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.areaA"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">b</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.areaB"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.areaC"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.countC"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">d</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.countD"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>

    <div v-if="isEditMode && afterParameterSetData != null">
      <div class="text-subtitle-1">編集後</div>
      <v-container class="leafArea">
        <v-row>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">樹冠葉面積モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">葉枚数モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">a</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.areaA"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">b</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.areaB"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.areaC"
                @change="onDisable"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.countC"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">d</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.countD"
                @change="onDisable"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>
  </v-container>
</template>

<script>
import {
  useLeafParamSetDetail,
  useLeafParamSetUpdate,
  useLeafParamSetAdd,
} from "@/api/TopStateGrowth/LAParameterSets/index";

export default {
  props: {
    shared: Object,
  },
  mounted() {
    this.shared.mount(this);
  },
  data() {
    return {
      isEditMode: false,
      beforeParameterSetData: {},
      afterParameterSetData: null,
    };
  },
  methods: {
    //*----------------------------
    // 表示モードの設定
    //*----------------------------
    setEditMode(isEditMode) {
      this.isEditMode = isEditMode;
      if (this.isEditMode) {
        if (null == this.afterParameterSetData) {
          this.afterParameterSetData = this.beforeParameterSetData;
        }
      }
    },
    //*----------------------------
    // 葉面積パラメータセット詳細取得
    //*----------------------------
    initialize(selectedId) {
      useLeafParamSetDetail(selectedId)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセット詳細取得に失敗しました。");
          } else {
            this.beforeParameterSetData = results.data;
            this.shared.onConclude(this.beforeParameterSetData); // 上位クラスと共有
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //*----------------------------
    // 追加処理
    //*----------------------------
    addData() {
      useLeafParamSetAdd(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセットの追加に失敗しました。");
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 更新処理
    //*----------------------------
    putData() {
      useLeafParamSetUpdate(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセットの更新に失敗しました。");
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>

<style>
.leafArea .v-input__control {
  width: 50px;
  padding: 0%;
}

.leafArea .v-input__slot {
  padding: 10px;
}
</style>