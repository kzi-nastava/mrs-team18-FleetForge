import { ChangeDetectorRef, Component } from '@angular/core';
import { Reports } from '../service/reports/reports';
import { CanvasJSAngularChartsModule } from '@canvasjs/angular-charts';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-reports',
  imports: [ CanvasJSAngularChartsModule,FormsModule,CommonModule],
  templateUrl: './admin-reports.component.html',
  styleUrl: './admin-reports.component.css',
})
export class AdminReportsComponent {
  email = '';
constructor(private reportsService: Reports, private cdr: ChangeDetectorRef) {}
  fromDate = '';
  toDate = '';
  totalPrice = '';
  averagePrice = '';
  totalRides: number | string = '';
  averageRides: number | string = '';
  totalDistance: number | string = '';
  averageDistance: number | string = '';
chart1: any;
chart2: any;
chart3: any;
 chartOptions1 = {
		title: {
    text: 'Ride Report',
    fontSize: 24,
    fontColor: '#333',
    fontFamily: 'Arial, sans-serif',
    fontWeight: 'bold',
    padding: 10
  },
  animationEnabled: true,
  axisX: {
    title: 'Time (days)',     
    includeZero: true,
    valueFormatString: "DD MMM YYYY",
    xValueType: "date"
  },
  axisY: {
    title: 'Number of Rides',   
    includeZero: true
  },
		data: [{
    type: "line",
    xValueType: "date",
    dataPoints: [] as { x: Date; y: number }[]
  }]
	}

    chartOptions2 = {
      title: {
      text: 'Distance Report',
      fontSize: 24,
      fontColor: '#333',
      fontFamily: 'Arial, sans-serif',
      fontWeight: 'bold',
      padding: 10
    },
    animationEnabled: true,
    axisX: {
      title: 'Time (days)',     
      includeZero: true,
      valueFormatString: "DD MMM YYYY",
      xValueType: "date"
    },
    axisY: {
      title: 'Distance (km)',   
      includeZero: true
    },
      data: [{
      type: "line",
      xValueType: "date",
      dataPoints: [] as { x: Date; y: number }[]
    }]
    }

  
  chartOptions3 = {
		title: {
    text: 'Price Report',
    fontSize: 24,
    fontColor: '#333',
    fontFamily: 'Arial, sans-serif',
    fontWeight: 'bold',
    padding: 10
  },
  animationEnabled: true,
  axisX: {
    title: 'Time (days)',     
    includeZero: true,
    valueFormatString: "DD MMM YYYY",
    xValueType: "date"
  },
  axisY: {
    title: 'Price (RSD)',   
    includeZero: true
  },
		data: [{
    type: "line",
    xValueType: "date",
    dataPoints: [] as { x: Date; y: number }[]
  }]
	}


  onSearch(): void {
    if (!this.fromDate || !this.toDate) {
    return;
  }
  if (new Date(this.fromDate) > new Date(this.toDate)) {
    alert('From date cannot be later than To date.');
    return;
  }
    this.chartOptions1.data[0].dataPoints = [];
    this.chartOptions2.data[0].dataPoints = [];
    this.chartOptions3.data[0].dataPoints = [];
    this.totalPrice = '0.00';
    this.averagePrice = '0.00';
    this.totalRides = 0;
    this.averageRides = 0;
    this.totalDistance = 0;
    this.averageDistance = 0;
    if(this.email.trim() === ''){
  this.reportsService.getAdminReportForAll(this.fromDate, this.toDate).subscribe({
    next: (response) => {
      let totalPrice = 0;
      let totalRides = 0;
      let totalDistance = 0;
      console.log('Report data:', response);
      const dataByDay = response?.dataByDay ?? {};

      for (const [date, rides] of Object.entries(dataByDay)) {
        const dateArr = (date as string).split('-').map(Number);
        const ridesForDay = Array.isArray(rides) ? rides : [];
        this.chartOptions1.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: ridesForDay.length});
        let totalKmForDay = 0;
        let totalPriceForDay = 0;
        ridesForDay.forEach((ride: any) => {
          const rideDistance = Number(ride?.totalDistance) || 0;
          const rideCost = Number(ride?.totalCost) || 0;

          totalKmForDay += rideDistance;
          totalPriceForDay += rideCost;
          totalPrice += rideCost;
          totalRides++;
          totalDistance += rideDistance;
        });
        this.chartOptions2.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: totalKmForDay});
        this.chartOptions3.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: totalPriceForDay});
        

      }
      this.chart1?.render();
          this.chart2?.render();
          this.chart3?.render();
      this.totalPrice = totalPrice.toFixed(2);
      this.totalRides = totalRides;
      this.totalDistance = totalDistance.toFixed(2);
      const totalDays = Object.keys(dataByDay).length;
      this.averageRides = totalDays > 0 ? (totalRides / totalDays).toFixed(2) : '0.00';
      this.averageDistance = totalDays > 0 ? (totalDistance / totalDays).toFixed(2) : '0.00';
      this.averagePrice = totalRides > 0 ? (totalPrice / totalRides).toFixed(2) : '0.00';
      this.cdr.detectChanges();
    },
    error: (error: any) => {
      console.error('Error fetching report data:', error);
      this.cdr.detectChanges();
      this.chart1?.render();
      this.chart2?.render();
      this.chart3?.render();
    }
  });

} else {
  this.reportsService.getAdminReportUser(this.fromDate, this.toDate, this.email).subscribe({
    next: (response) => {
       let totalPrice = 0;
      let totalRides = 0;
      let totalDistance = 0;
      console.log('Report data:', response);
      const dataByDay = response?.dataByDay ?? {};

      for (const [date, rides] of Object.entries(dataByDay)) {
        const dateArr = (date as string).split('-').map(Number);
        const ridesForDay = Array.isArray(rides) ? rides : [];
        this.chartOptions1.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: ridesForDay.length});
        let totalKmForDay = 0;
        let totalPriceForDay = 0;
        ridesForDay.forEach((ride: any) => {
          const rideDistance = Number(ride?.totalDistance) || 0;
          const rideCost = Number(ride?.totalCost) || 0;

          totalKmForDay += rideDistance;
          totalPriceForDay += rideCost;
          totalPrice += rideCost;
          totalRides++;
          totalDistance += rideDistance;
        });
        this.chartOptions2.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: totalKmForDay});
        this.chartOptions3.data[0].dataPoints.push({ x: new Date(dateArr[0], dateArr[1] - 1, dateArr[2]), y: totalPriceForDay});
        

      }
      this.chart1?.render();
          this.chart2?.render();
          this.chart3?.render();
      this.totalPrice = totalPrice.toFixed(2);
      this.totalRides = totalRides;
      this.totalDistance = totalDistance.toFixed(2);
      const totalDays = Object.keys(dataByDay).length;
      this.averageRides = totalDays > 0 ? (totalRides / totalDays).toFixed(2) : '0.00';
      this.averageDistance = totalDays > 0 ? (totalDistance / totalDays).toFixed(2) : '0.00';
      this.averagePrice = totalRides > 0 ? (totalPrice / totalRides).toFixed(2) : '0.00';
      this.cdr.detectChanges();
    },
    error: (error: any) => {
      console.error('Error fetching report data:', error);
      this.cdr.detectChanges();
      this.chart1?.render();
      this.chart2?.render();
      this.chart3?.render();
    }
  });
}}
}
