<template>
  <div>
    <v-expand-transition>
      <template>
        <v-container>
          <v-row class="mt-1 mb-1">
            <v-col>
              <v-btn
                class="ml-1"
                v-for="(button, index) in editButtons"
                depressed
                color="primary"
                elevation="3"
                :key="index"
                @click="openDialog(button)"
              >
                {{ button.name }}
              </v-btn>
            </v-col>
          </v-row>
        </v-container>
      </template>
    </v-expand-transition>
    <div>
      <GEActualValueInput ref="refActualValueInput" :shared="shared" />
      <GEParameterSets ref="refGEParameterSets" :shared="shared" />
      <!--<LAActualValueInput ref="refLAActualValueInput" :shared="shared" />
  -    <LAParameterSets ref="refGEParameterSets" :shared="shared" />-->
    </div>
    <!--    <div v-if="modelId==3">
      <PEActualValueInput ref="refActualValueInput" :shared="shared" />
      <PEParameterSets ref="refGEParameterSets" :shared="shared" />
    </div>-->
  </div>
</template>
<script>
import { DialogController } from "@/lib/mountController.js";
import GEActualValueInput from "@/components/TopStageGrowth/GEActualValueInput";
import allEditButtons from "@/components/TopStageGrowth/hooks/editButtons.json";
import GEParameterSets from "@/components/TopStageGrowth/GEParameterSets";
//import ReferenceFValue from "@/components/TopStageGrowth/ReferenceFValue";
//import LAActualValueInput from "@/components/TopStageGrowth/Input";
//import LAParameterSets from "@/components/TopStageGrowth/LAParameterSets";
//import PEActualValueInput from "@/components/TopStageGrowth/PEActualValueInput";
//import PEParameterSets from "@/components/TopStageGrowth/PEParameterSets";
export default {
  data() {
    return {
      //* モデルID
      // modelId: 1,
      editButtons: [],

      shared: new DialogController(),
      selectedMenu: null,
      selectedField: [],
      selectedYears: [],
      selectedDevices: [],
    };
  },
  components: {
    GEActualValueInput,
    GEParameterSets,
    //    ReferenceFValue,
    //  LAActualValueInput,
    // LAParameterSets,
    // PEActualValueInput,
    // PEParameterSets,
  },
  methods: {
    setControll(modelId) {
      const editButtons = allEditButtons[modelId].buttons;
      this.editButtons.splice(0);
      this.editButtons.push(...editButtons);
    },

    setUpDialog(component) {
 //     this.shared.reset();
      this.shared.setUp(
        component,
        function (component) {
          component.initialize();
        }.bind(this),
        function () {}.bind(this)
      );
    },

    openDialog: function (item) {
      this.setUpDialog(this.$refs[item.key]);
    },
  },
};
</script>