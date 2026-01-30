import type { ChangeEventHandler, ReactNode } from 'react';
import MenuItem from '@mui/material/MenuItem';
import MuiTextField from '@mui/material/TextField';

export type SelectOption<T extends string = string> = {
  value: T;
  label: string;
};

type SelectFieldProps = {
  label: string;
  value: string | number;
  onChange: ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement>;
  helpText?: string;
  error?: string;
  options?: SelectOption[];
  children?: ReactNode;
  required?: boolean;
  disabled?: boolean;
};

const SelectField = ({
  label,
  value,
  onChange,
  helpText,
  error,
  options,
  children,
  required,
  disabled,
}: SelectFieldProps) => {
  return (
    <MuiTextField
      select
      label={label}
      value={value}
      onChange={onChange}
      helperText={error || helpText}
      error={Boolean(error)}
      required={required}
      disabled={disabled}
      size="small"
      fullWidth
    >
      {options
        ? options.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))
        : children}
    </MuiTextField>
  );
};

export default SelectField;
