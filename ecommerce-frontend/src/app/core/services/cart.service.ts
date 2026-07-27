import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface CartItem {
  product: any;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSubject = new BehaviorSubject<CartItem[]>([]);
  public cartItems$ = this.cartItemsSubject.asObservable();
  
  private authService = inject(AuthService);
  private currentCartKey = 'cart_guest';

  constructor() {
    this.authService.currentUser$.subscribe(username => {
      if (username) {
        this.handleUserLogin(`cart_${username}`);
      } else {
        this.currentCartKey = 'cart_guest';
        this.cartItemsSubject.next(this.loadCart());
      }
    });
  }

  private handleUserLogin(userCartKey: string): void {
    const guestCart = this.loadCartKey('cart_guest');
    this.currentCartKey = userCartKey;
    const userCart = this.loadCartKey(userCartKey);
    
    // Merge guest cart into user cart if guest cart has items
    if (guestCart.length > 0) {
      guestCart.forEach(guestItem => {
        const existingItem = userCart.find(ui => ui.product.id === guestItem.product.id);
        if (existingItem) {
          existingItem.quantity += guestItem.quantity;
        } else {
          userCart.push(guestItem);
        }
      });
      // Clear guest cart after merge
      localStorage.removeItem('cart_guest');
      this.saveCart(userCart);
    } else {
      // Just load the user's cart
      this.cartItemsSubject.next(userCart);
    }
  }

  private loadCartKey(key: string): CartItem[] {
    const saved = localStorage.getItem(key);
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        return [];
      }
    }
    return [];
  }

  private loadCart(): CartItem[] {
    return this.loadCartKey(this.currentCartKey);
  }

  private saveCart(items: CartItem[]): void {
    localStorage.setItem(this.currentCartKey, JSON.stringify(items));
    this.cartItemsSubject.next(items);
  }

  addToCart(product: any, quantity: number = 1): void {
    const currentCart = this.cartItemsSubject.value;
    const existing = currentCart.find(item => item.product.id === product.id);

    if (existing) {
      existing.quantity += quantity;
      this.saveCart([...currentCart]);
    } else {
      this.saveCart([...currentCart, { product, quantity }]);
    }
  }

  removeFromCart(productId: number): void {
    const currentCart = this.cartItemsSubject.value;
    const updatedCart = currentCart.filter(item => item.product.id !== productId);
    this.saveCart(updatedCart);
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    const currentCart = this.cartItemsSubject.value;
    const existing = currentCart.find(item => item.product.id === productId);
    if (existing) {
      existing.quantity = quantity;
      this.saveCart([...currentCart]);
    }
  }

  clearCart(): void {
    this.saveCart([]);
  }

  getCartTotal(): number {
    return this.cartItemsSubject.value.reduce((total, item) => total + (item.product.price * item.quantity), 0);
  }

  getCartItemCount(): number {
    return this.cartItemsSubject.value.reduce((count, item) => count + item.quantity, 0);
  }
}
