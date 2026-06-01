'use client';

import { Globe } from 'lucide-react';

export function Footer() {
  return (
    <footer className="bg-background border-t border-border animate-fade-in">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          {/* Brand */}
          <div className="animate-slide-up stagger-1">
            <div className="flex items-center gap-2 mb-4">
              <Globe className="w-6 h-6 text-primary" />
              <span className="font-semibold text-foreground">LinguaAI</span>
            </div>
            <p className="text-muted-foreground text-sm">
              Breaking language barriers with AI-powered translation.
            </p>
          </div>

          {/* Product */}
          <div className="animate-slide-up stagger-2">
            <h4 className="font-semibold text-foreground mb-3 text-sm">Product</h4>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>
                <a href="#features" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Features
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Pricing
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  API Docs
                </a>
              </li>
            </ul>
          </div>

          {/* Company */}
          <div className="animate-slide-up stagger-3">
            <h4 className="font-semibold text-foreground mb-3 text-sm">Company</h4>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  About
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Blog
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Contact
                </a>
              </li>
            </ul>
          </div>

          {/* Legal */}
          <div className="animate-slide-up stagger-4">
            <h4 className="font-semibold text-foreground mb-3 text-sm">Legal</h4>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Privacy
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Terms
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-foreground transition-all duration-200 hover:translate-x-1 inline-block">
                  Security
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom */}
        <div className="border-t border-border pt-8 flex flex-col sm:flex-row items-center justify-between text-xs text-muted-foreground animate-fade-in stagger-5">
          <div>&copy; 2024 LinguaAI. All rights reserved.</div>
          <div className="flex gap-6 mt-4 sm:mt-0">
            <a href="#" className="hover:text-foreground transition-all duration-200 hover:scale-105 inline-block">
              Twitter
            </a>
            <a href="#" className="hover:text-foreground transition-all duration-200 hover:scale-105 inline-block">
              GitHub
            </a>
            <a href="#" className="hover:text-foreground transition-all duration-200 hover:scale-105 inline-block">
              LinkedIn
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
