import Label from "@/models/element/Label";

type CensorUnit = Label & {
  modelNumber: string;
  displayName: string;
  stemDiameter: string;
};

export default CensorUnit;
