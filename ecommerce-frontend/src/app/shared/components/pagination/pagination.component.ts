import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="pagination-container d-flex justify-center align-center mt-5 mb-4 gap-3">
      <button class="btn-secondary" (click)="onPrev()" [disabled]="currentPage === 0">
        &laquo; Previous
      </button>
      
      <span class="page-info text-secondary fw-bold">
        Page {{ currentPage + 1 }} of {{ totalPages }}
      </span>
      
      <button class="btn-secondary" (click)="onNext()" [disabled]="currentPage >= totalPages - 1">
        Next &raquo;
      </button>
    </div>
  `
})
export class PaginationComponent {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  
  @Output() pageChange = new EventEmitter<number>();

  onPrev(): void {
    if (this.currentPage > 0) {
      this.pageChange.emit(this.currentPage - 1);
    }
  }

  onNext(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.pageChange.emit(this.currentPage + 1);
    }
  }
}
