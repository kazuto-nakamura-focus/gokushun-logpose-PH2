import DeviceInfoDTO from "@/models/dtos/DeviceInfoDTO";
import CensorItem from "@/models/element/CensorItem";

type DTO = DeviceInfoDTO & {
  censorItems: Array<CensorItem>;
};

export default DTO;
