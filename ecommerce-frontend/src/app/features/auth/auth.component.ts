import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  isLogin = true;
  isLoading = false;
  errorMsg = '';
  
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  authForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
    firstName: [''],
    lastName: [''],
    email: ['']
  });

  toggleMode() {
    this.isLogin = !this.isLogin;
    this.errorMsg = '';
    this.authForm.reset();
  }

  onSubmit() {
    if (this.authForm.invalid) return;

    this.isLoading = true;
    this.errorMsg = '';
    const val = this.authForm.value;

    if (this.isLogin) {
      this.authService.login({ username: val.username, password: val.password }).subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.errorMsg = 'Login failed. Please check your credentials.';
          this.isLoading = false;
        }
      });
    } else {
      this.authService.register(val).subscribe({
        next: () => {
          this.isLoading = false;
          alert('Registration successful! Please login.');
          this.toggleMode();
        },
        error: (err) => {
          this.errorMsg = 'Registration failed. Try a different username.';
          this.isLoading = false;
        }
      });
    }
  }
}
