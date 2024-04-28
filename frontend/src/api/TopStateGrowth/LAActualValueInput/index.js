import axios from "@/lib/axiosHooks";

//新梢数取得処理
const useLeafValueShootDetail = (deviceId, year, date) => {
  const config = {
    params: {
      deviceId,
      year,
      date
    },
  };
  //->LeafShootDTO
  return axios.get("/leaf/value/shoot", config);
};
//新梢辺り葉枚数・平均個葉面積検索処理
const useLeafModelValue = (deviceId, year, date) => {
  const config = {
    params: {
      deviceId,
      year,
      date,
    },
  };
  //->LeafvaluesDTO
  return axios.get("/leaf/value/model", config);
};
//新梢辺り葉枚数・平均個葉面積検索処理
const useLeafValueAllAreaAndCountDetail = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year,
    },
  };
  //->LeafvaluesDTO
  return axios.get("/leaf/value/allAreaAndCount", config);
};

//新梢数の登録
//->LeafShootDTO
const useLeafValueShoot = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/leaf/value/shoot", data, config);
};

//新梢辺り葉枚数・平均個葉面積登録処理
//->LeafvaluesDTO
const useLeafValueAreaAndCount = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/leaf/value/areaAndCount", data, config);
};

export {
  useLeafValueShootDetail,
  useLeafModelValue,
  useLeafValueAllAreaAndCountDetail,
  useLeafValueShoot,
  useLeafValueAreaAndCount
};
