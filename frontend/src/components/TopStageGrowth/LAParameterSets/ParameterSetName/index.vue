<template>
  <v-container>
    <div>
      <div class="text-subtitle-1">パラメータセット名</div>
      <v-select
        v-model="selectedId"
        :items="parameterList"
        item-text="name"
        item-value="id"
        class="select_size ml-1"
        @change="getItems"
        v-if="isEditMode"
      ></v-select>
      <v-subheader class="ma-0 pa-0" v-if="!isEditMode">{{
        leafAreaData.leafAreaTitle
      }}</v-subheader>
      <div>
        <div class="text-subtitle-1" v-if="isEditMode">編集前</div>

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
                  :value="leafAreaData.leafAreaAA"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">b</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="leafAreaData.leafAreaAB"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">c</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="leafAreaData.leafAreaAC"
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
                  :value="leafAreaData.leafAreaNC"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="leafAreaData.leafAreaND"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
      <div v-if="!isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="leafAreaData.content"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="leafAreaData.author"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
      </div>

      <div v-if="isEditMode">
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
                  v-model.number="afterParameterSetData.leafAreaAA"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">b</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.leafAreaAB"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">c</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.leafAreaAC"
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
                  v-model.number="afterParameterSetData.leafAreaNC"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.leafAreaND"
                  @change="onDisable"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
      <div v-if="isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleContents]"
          v-model.number="afterParameterSetData.content"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleAuthor]"
          v-model.number="afterParameterSetData.author"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
      </div>
    </div>
    <v-card-actions>
      <v-spacer></v-spacer>
      <div v-if="!isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="overwriteSave"
          :disabled="isDisabledOverrideBtn"
          >上書き保存</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="aliasSave"
          >別名保存</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="deleteParameterSet"
          :disabled="isDisabledDeleteBtn"
          >削除</v-btn
        >
      </div>
    </v-card-actions>
    <v-card-actions>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
    </v-card-actions>

    <ParameterSetNameDialog
      :handleSubmit="handleSubmitParameterSetName"
      :show="isShowParameterSetName"
      confirmText="保存"
      cancelText="キャンセル"
      ref="refParameterSetName"
    />
  </v-container>
</template>

<script>
import ParameterSetNameDialog from "@/components/parts/ParameterSetName";

export default {
  name: "ParameterSetName",
  props: {
    shared: Object,
    leafAreaData: Object,
    isEditMode: Boolean,
    parameterSetList: Array,
  },
  created() {},
  updated() {},
  mounted() {
    this.shared.mount(this);
  },
  data() {
    return {
      // isEditMode: this.isEditMode,
      // parameterSetLabel: "パラメータセット名",
      parameterList: this.parameterSetList,
      beforeParameterSetData: this.leafAreaData,
      afterParameterSetData: {
        leafAreaTitle: this.leafAreaData.leafAreaTitle,
        leafAreaAA: 0,
        leafAreaAB: 0,
        leafAreaAC: 0,
        leafAreaNC: 0,
        leafAreaND: 0,
        content: this.leafAreaData.content,
        author: this.leafAreaData.author,
      },
      // ruleParameterSetData: (value) => !!value || "パラメータデータを入力してください。",
      ruleContents: (value) => !!value || "説明を入力してください。",
      ruleAuthor: (value) => !!value || "編集者を入力してください。",

      isShowParameterSetName: false,
      isDisabledDeleteBtn: false,
      isDisabledOverrideBtn: false,
      selectedId: this.leafAreaData.id,
    };
  },
  methods: {
    initialize(list) {
      this.parameterList = list;
    },
    deleteParameterSet() {
      // alert("削除")
      this.$emit("deleteParameterSet", this.beforeParameterSetData);
    },
    close() {
      // alert("close")
      this.$emit("close");
    },
    overwriteSave() {
      // alert("overwriteSave")
      this.$emit("overwriteSave", this.afterParameterSetData);
    },
    aliasSave() {
      // alert("aliasSave")
      // this.$emit("aliasSave", this.afterParameterSetData)
      this.$refs.refParameterSetName.display();
    },
    onDisable() {
      this.isDisabledDeleteBtn = true;
    },
    getItems() {
      // console.log("getItems")
      // console.log("selectedId: ", this.selectedId)
      let selectSetName = "";
      for (let parameter of this.parameterList) {
        if (this.selectedId == parameter.id) {
          selectSetName = parameter.name;
        }
      }
      // console.log("selectSetName: ", selectSetName)
      if (selectSetName.indexOf("デフォルト") >= 0) {
        this.isDisabledDeleteBtn = true;
        this.isDisabledOverrideBtn = true;
      } else {
        this.isDisabledDeleteBtn = false;
        this.isDisabledOverrideBtn = false;
      }
      this.$emit("getItems", this.selectedId);
    },
    //パラメータセット名の外部から制御
    handleSubmitParameterSetName: function (name) {
      //APIリクエスト処理を追記
      // console.log(name);
      this.afterParameterSetData.leafAreaTitle = name;
      this.$emit("aliasSave", this.afterParameterSetData);
      this.$refs.refParameterSetName.close();
    },
  },
  watch: {
    // 'afterParameterSetData.leafAreaBeforeD': function (changeAfter) {
    //     //isNaN(changeAfter)
    //     if (this.check(changeAfter)) {
    //         console.log("0000")
    //     } else {
    //         console.log("111")
    //     }
    // }
  },
  components: {
    ParameterSetNameDialog,
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