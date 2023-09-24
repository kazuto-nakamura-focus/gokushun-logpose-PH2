import SelectedTargetID from "@/models/element/SelectedTargetID";
import FDataDTO from "@/models/dtos/FDataDTO";

type DTO = SelectedTargetID & {
  list: Array<FDataDTO>;
};

export default DTO;
