import SelectedTarget from "@/models/element/SelectedTarget";

type DTO = {
  selected: SelectedTarget;
  YStart: number;
  YEnd: number;
  XStart: number;
  XEnd: number;
  years: Array<number>;
};

export default DTO;
