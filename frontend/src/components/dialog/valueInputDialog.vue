<template>
  <v-app>
    <v-dialog persistent v-model="isDialog" width="auto">
      <div class="dailogPanelBackground">
        <v-card elevation="2">
          <div class="dailogTitlePanel">
            {{ title }}
          </div>
          <v-select
            v-model="selected"
            :items="fields"
            solo
            hide-details="auto"
          ></v-select>
          <v-select
            v-model="param"
            :items="params"
            item-text="name"
            item-value="name"
            solo
            return-object
          ></v-select>
          <div class="GS_input_line">
            <date-picker
              v-model="date"
              valueType="format"
              style="width: 80%"
            ></date-picker>
          </div>
          <div class="GS_input_line">
            <v-text-field
              v-model.trim="value"
              label="値"
              return-object
              style="width: 60%"
            ></v-text-field>
            <div class="GS_unit_text">{{param.unit}}</div>
          </div>

          <div class="GS_ButtonArea">
            <v-btn
              color="blue"
              class="ma-2 white--text"
              elevation="2"
              @click="save()"
              >保存</v-btn
            >

            <svg-icon
              type="mdi"
              :path="path"
              :size="48"
              style="color: gray; margin-top: 0.1rem; cursor: pointer"
              v-on:click.native="close()"
            ></svg-icon>
          </div>
        </v-card>
      </div>
    </v-dialog>
  </v-app>
</template>
<script>
import moment from "moment";
import SvgIcon from "@jamescoyle/vue-icon";
import { mdiExitToApp } from "@mdi/js";

export default {
  props: { shared /** MountController */: { required: true } },

  data() {
    return {
      value: "",
      date: moment().format("YYYY-MM-DD"),
      isDialog: false,
      title: "", // 選ばれたモデル種別
      fields: [], // 選ばれた圃場
      selected: null,
      param: null,
      params: [],
      path: mdiExitToApp,
    };
  },

  components: {
    SvgIcon,
  },

  mounted() {
    this.shared.mount(this);
  },
  methods: {
    initialize: function (title, fields, params) {
      // タイトル
      this.title = title;
      // 圃場リスト
      this.fields = [];
      for (const item of fields) {
        if (item.state) this.fields.push(item.title);
      }
      this.selected = this.fields[0];
      // デバイス
      this.params = params;
      console.log(this.params);
      this.param = this.params[0];
      this.isDialog = true;
    },
    close: function () {
      this.isDialog = false;
    },
    save: function () {
      this.isDialog = false;
      this.shared.onConclude(this.value);
    },

  },
};
</script>
<style lang="scss" scoped>
@import "@/style/common.css";
.GS_unit_text {
  margin-top: 1.7rem;
  font-style: italic;
  color: gray;
  font-size: 0.8em;
  width: auto;
}

.container {
  display: flex;
  flex-flow: wrap;
  align-items: center;
}
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: 90%;
  flex-wrap: wrap;
}
</style>
