import axios from "@/lib/axiosHooks";
import axiosForMapAPI from "@/lib/axiosHooks/ExternalApi.js";

//圃場一覧取得
const useFieldList = () => {
  const config = {
    params: {},
  };
  return axios.get("/field/list", config);
};

//圃場情報追加
const useFieldInfoAdd = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/field/info", data, config);
};

//圃場情報更新
const useFieldInfoUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/field/info", data, config);
};

//圃場情報詳細取得
const useFieldInfo = (fieldId) => {
  const config = {
    params: { fieldId },
  };
  return axios.get("/field/info", config);
};

//圃場削除
const useFieldInfoRemove = (fieldId) => {
  const config = {
    params: { fieldId },
  };
  return axios.delete("/field/info", config);
};

//緯度経度取得
const useMapLocation = (q) => {
  const config = {
    params: { q },
  };
  return axiosForMapAPI.get("/AddressSearch", config);
};

export {
  useFieldList,
  useFieldInfo,
  useFieldInfoAdd,
  useFieldInfoUpdate,
  useFieldInfoRemove,
  useMapLocation,
};
