import Axios from "axios";
import { axiosEnvConfig } from "@/config/envConfig";

const axios = Axios.create({
  baseURL: axiosEnvConfig.baseUrl,
  headers: {
    "Content-Type": "application/json",
    //   "X-Requested-With": "XMLHttpRequest",
    //    "Access-Control-Allow-Origin": "*",
  },
  withCredentials: true,
});

export default axios;
