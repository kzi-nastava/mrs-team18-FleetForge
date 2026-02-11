import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PriceConfigurationService } from '../../shared/services/price-configuration.service';
import { PriceConfigurationDTO, UpdatePriceConfigurationDTO } from '../../shared/dtos/price-configuration.dtos';
import { VehicleType } from '../../shared/models/vehicle.model';

interface PriceForm {
  vehicleType: VehicleType;
  form: FormGroup;
  original: PriceConfigurationDTO | null;
  isModified: boolean;
  isSaving: boolean;
}

@Component({
  selector: 'app-price-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './price-management.component.html',
  styleUrls: ['./price-management.component.css']
})
export class PriceManagementComponent implements OnInit {
  priceForms: PriceForm[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  vehicleTypes = Object.values(VehicleType);

  vehicleTypeLabels: { [key in VehicleType]: string } = {
    [VehicleType.STANDARD]: 'Standard',
    [VehicleType.LUXURY]: 'Luxury',
    [VehicleType.VAN]: 'Van'
  };

  constructor(
    private fb: FormBuilder,
    private priceConfigurationService: PriceConfigurationService
  ) {}

  ngOnInit(): void {
    this.loadPriceConfigurations();
  }

  loadPriceConfigurations(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.priceConfigurationService.getAllPriceConfigurations().subscribe({
      next: (configurations) => {
        this.priceForms = configurations.map(config => ({
          vehicleType: config.vehicleType,
          form: this.createForm(config),
          original: config,
          isModified: false,
          isSaving: false
        }));
        this.isLoading = false;
        this.setupFormChangeListeners();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load price configurations. Please try again.';
        this.isLoading = false;
        console.error('Error loading price configurations:', error);
      }
    });
  }

  createForm(config: PriceConfigurationDTO): FormGroup {
    return this.fb.group({
      basePrice: [config.basePrice, [Validators.required, Validators.min(0.01)]],
      pricePerKm: [config.pricePerKm, [Validators.required, Validators.min(0.01)]]
    });
  }

  setupFormChangeListeners(): void {
    this.priceForms.forEach(priceForm => {
      priceForm.form.valueChanges.subscribe(() => {
        priceForm.isModified = this.isFormModified(priceForm);
      });
    });
  }

  isFormModified(priceForm: PriceForm): boolean {
    if (!priceForm.original) return false;
    
    const currentValues = priceForm.form.value;
    return (
      currentValues.basePrice !== priceForm.original.basePrice ||
      currentValues.pricePerKm !== priceForm.original.pricePerKm
    );
  }

  savePriceConfiguration(priceForm: PriceForm): void {
    if (priceForm.form.invalid || !priceForm.isModified) {
      return;
    }

    priceForm.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const updateDto: UpdatePriceConfigurationDTO = {
      basePrice: priceForm.form.value.basePrice,
      pricePerKm: priceForm.form.value.pricePerKm
    };

    this.priceConfigurationService
      .updatePriceConfiguration(priceForm.vehicleType, updateDto)
      .subscribe({
        next: (updated) => {
          priceForm.original = updated;
          priceForm.isModified = false;
          priceForm.isSaving = false;
          this.successMessage = `${this.vehicleTypeLabels[priceForm.vehicleType]} pricing updated successfully!`;
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          priceForm.isSaving = false;
          this.errorMessage = `Failed to update ${this.vehicleTypeLabels[priceForm.vehicleType]} pricing. Please try again.`;
          console.error('Error updating price configuration:', error);
        }
      });
  }

  resetForm(priceForm: PriceForm): void {
    if (priceForm.original) {
      priceForm.form.patchValue({
        basePrice: priceForm.original.basePrice,
        pricePerKm: priceForm.original.pricePerKm
      });
      priceForm.isModified = false;
    }
  }

  getVehicleTypeIcon(type: VehicleType): string {
    switch (type) {
      case VehicleType.STANDARD:
        return '🚗';
      case VehicleType.LUXURY:
        return '🚙';
      case VehicleType.VAN:
        return '🚐';
      default:
        return '🚗';
    }
  }
}
