import axios from "@/lib/axiosHooks";
import { AuthCookies } from "@/lib/AuthCookies.js";

//デバイス一覧取得
const useDeviceList = () => {
  const config = {
    params: {},
  };
  let cookies = new AuthCookies();
  cookies.removeByPath("at", "/api/device");
  return axios.get("/device/list", config);
};

//デバイス情報追加
const useDeviceInfoAdd = (data) => {
  const config = {
    params: {},
  };

  return axios.post("/device/info", data, config);
};

//デバイス情報更新
const useDeviceInfoUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/device/info", data, config);
};

//デバイス情報詳細取得
const useDeviceInfo = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.get("/device/info", config);
};

//デバイス削除
const useDeviceInfoRemove = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.delete("/device/info", config);
};

//デバイスマスターの取得
const useDeviceMastersAPI = () => {
  const config = {
    params: {},
  };
  return axios.get("/device/masters", config);
};

//ロード対象の取得
const useGetSchedule = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.get("/bulk/load/schedule", config);
};

// デバイスデータのロード
const useLoadData = (deviceId) => {
  const config = {
    params: {},
    timeout: 900000
  };
  return axios.get("/bulk/load/device/" + deviceId, config);
};
// デバイスデータのロード
const useGetAllDataLoadStatus = (date) => {
  const config = {
    params: { date },
  };
  return axios.get("/bulk/load/info", config);
};

export {
  useDeviceList,
  useDeviceInfoRemove,
  useDeviceInfo,
  useDeviceInfoAdd,
  useDeviceInfoUpdate,
  useDeviceMastersAPI,
  useGetSchedule,
  useLoadData,
  useGetAllDataLoadStatus,
};
