import axios from "@/lib/axiosHooks";

//着果量着果負担パラメータセットリスト取得（未使用）
const useFruitParamSetList = () => {
  const config = {
    params: {},
  };
  return axios.get("/fruit/paramSet/list", config);
};

//着果量着果負担パラメータセット詳細取得（未使用）
const useFruitParamSetDetail = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.get("/fruit/paramSet/detail", config);
};

//着果量着果負担パラメータセット更新履歴取得（未使用）
const useFruitParamSetHistory = (paramSetId) => {
  const config = {
    params: { paramSetId },
  };
  return axios.get("/fruit/paramSet/history", config);
};

//着果量着果負担パラメータセット更新（未使用）
const useFruitParamSetUpdate = () => {
  const config = {
    params: {},
  };
  return axios.get("/fruit/paramSet", config);
};

//着果量着果負担パラメータセット追加（未使用）
const useFruitParamSetAdd = () => {
  const config = {
    params: {},
  };
  return axios.get("/fruit/paramSet", config);
};

//着果量着果負担パラメータセット削除（未使用）
const useFruitParamSetRemove = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.delete("/fruit/paramSet", config);
};

export {
  useFruitParamSetList,
  useFruitParamSetDetail,
  useFruitParamSetHistory,
  useFruitParamSetUpdate,
  useFruitParamSetAdd,
  useFruitParamSetRemove,
};
