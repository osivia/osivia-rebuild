/*     */ package org.apache.catalina.deploy;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class SecurityConstraint
/*     */   implements Serializable
/*     */ {
/*  62 */   private boolean allRoles = false;
/*     */ 
/*  71 */   private boolean authConstraint = false;
/*     */ 
/*  78 */   private String[] authRoles = new String[0];
/*     */ 
/*  85 */   private SecurityCollection[] collections = new SecurityCollection[0];
/*     */ 
/*  91 */   private String displayName = null;
/*     */ 
/*  98 */   private String userConstraint = "NONE";
/*     */ 
/*     */   public boolean getAllRoles()
/*     */   {
/* 110 */     return this.allRoles;
/*     */   }
/*     */ 
/*     */   public boolean getAuthConstraint()
/*     */   {
/* 121 */     return this.authConstraint;
/*     */   }
/*     */ 
/*     */   public void setAuthConstraint(boolean authConstraint)
/*     */   {
/* 132 */     this.authConstraint = authConstraint;
/*     */   }
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/* 142 */     return this.displayName;
/*     */   }
/*     */ 
/*     */   public void setDisplayName(String displayName)
/*     */   {
/* 152 */     this.displayName = displayName;
/*     */   }
/*     */ 
/*     */   public String getUserConstraint()
/*     */   {
/* 162 */     return this.userConstraint;
/*     */   }
/*     */ 
/*     */   public void setUserConstraint(String userConstraint)
/*     */   {
/* 174 */     if (userConstraint != null)
/* 175 */       this.userConstraint = userConstraint;
/*     */   }
/*     */ 
/*     */   public void addAuthRole(String authRole)
/*     */   {
/* 191 */     if (authRole == null)
/* 192 */       return;
/* 193 */     if ("*".equals(authRole)) {
/* 194 */       this.allRoles = true;
/* 195 */       return;
/*     */     }
/* 197 */     String[] results = new String[this.authRoles.length + 1];
/* 198 */     for (int i = 0; i < this.authRoles.length; i++)
/* 199 */       results[i] = this.authRoles[i];
/* 200 */     results[this.authRoles.length] = authRole;
/* 201 */     this.authRoles = results;
/* 202 */     this.authConstraint = true;
/*     */   }
/*     */ 
/*     */   public void addCollection(SecurityCollection collection)
/*     */   {
/* 215 */     if (collection == null)
/* 216 */       return;
/* 217 */     SecurityCollection[] results = new SecurityCollection[this.collections.length + 1];
/*     */ 
/* 219 */     for (int i = 0; i < this.collections.length; i++)
/* 220 */       results[i] = this.collections[i];
/* 221 */     results[this.collections.length] = collection;
/* 222 */     this.collections = results;
/*     */   }
/*     */ 
/*     */   public boolean findAuthRole(String role)
/*     */   {
/* 235 */     if (role == null)
/* 236 */       return false;
/* 237 */     for (int i = 0; i < this.authRoles.length; i++) {
/* 238 */       if (role.equals(this.authRoles[i]))
/* 239 */         return true;
/*     */     }
/* 241 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] findAuthRoles()
/*     */   {
/* 254 */     return this.authRoles;
/*     */   }
/*     */ 
/*     */   public SecurityCollection findCollection(String name)
/*     */   {
/* 267 */     if (name == null)
/* 268 */       return null;
/* 269 */     for (int i = 0; i < this.collections.length; i++) {
/* 270 */       if (name.equals(this.collections[i].getName()))
/* 271 */         return this.collections[i];
/*     */     }
/* 273 */     return null;
/*     */   }
/*     */ 
/*     */   public SecurityCollection[] findCollections()
/*     */   {
/* 285 */     return this.collections;
/*     */   }
/*     */ 
/*     */   public boolean included(String uri, String method)
/*     */   {
/* 300 */     if (method == null) {
/* 301 */       return false;
/*     */     }
/*     */ 
/* 304 */     for (int i = 0; i < this.collections.length; i++) {
/* 305 */       if (!this.collections[i].findMethod(method))
/*     */         continue;
/* 307 */       String[] patterns = this.collections[i].findPatterns();
/* 308 */       for (int j = 0; j < patterns.length; j++) {
/* 309 */         if (matchPattern(uri, patterns[j])) {
/* 310 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 315 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAuthRole(String authRole)
/*     */   {
/* 328 */     if (authRole == null)
/* 329 */       return;
/* 330 */     int n = -1;
/* 331 */     for (int i = 0; i < this.authRoles.length; i++) {
/* 332 */       if (this.authRoles[i].equals(authRole)) {
/* 333 */         n = i;
/* 334 */         break;
/*     */       }
/*     */     }
/* 337 */     if (n >= 0) {
/* 338 */       int j = 0;
/* 339 */       String[] results = new String[this.authRoles.length - 1];
/* 340 */       for (int i = 0; i < this.authRoles.length; i++) {
/* 341 */         if (i != n)
/* 342 */           results[(j++)] = this.authRoles[i];
/*     */       }
/* 344 */       this.authRoles = results;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeCollection(SecurityCollection collection)
/*     */   {
/* 358 */     if (collection == null)
/* 359 */       return;
/* 360 */     int n = -1;
/* 361 */     for (int i = 0; i < this.collections.length; i++) {
/* 362 */       if (this.collections[i].equals(collection)) {
/* 363 */         n = i;
/* 364 */         break;
/*     */       }
/*     */     }
/* 367 */     if (n >= 0) {
/* 368 */       int j = 0;
/* 369 */       SecurityCollection[] results = new SecurityCollection[this.collections.length - 1];
/*     */ 
/* 371 */       for (int i = 0; i < this.collections.length; i++) {
/* 372 */         if (i != n)
/* 373 */           results[(j++)] = this.collections[i];
/*     */       }
/* 375 */       this.collections = results;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 386 */     StringBuffer sb = new StringBuffer("SecurityConstraint[");
/* 387 */     for (int i = 0; i < this.collections.length; i++) {
/* 388 */       if (i > 0)
/* 389 */         sb.append(", ");
/* 390 */       sb.append(this.collections[i].getName());
/*     */     }
/* 392 */     sb.append("]");
/* 393 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private boolean matchPattern(String path, String pattern)
/*     */   {
/* 413 */     if ((path == null) || (path.length() == 0))
/* 414 */       path = "/";
/* 415 */     if ((pattern == null) || (pattern.length() == 0)) {
/* 416 */       pattern = "/";
/*     */     }
/*     */ 
/* 419 */     if (path.equals(pattern)) {
/* 420 */       return true;
/*     */     }
/*     */ 
/* 423 */     if ((pattern.startsWith("/")) && (pattern.endsWith("/*"))) {
/* 424 */       pattern = pattern.substring(0, pattern.length() - 2);
/* 425 */       if (pattern.length() == 0)
/* 426 */         return true;
/* 427 */       if (path.endsWith("/"))
/* 428 */         path = path.substring(0, path.length() - 1);
/*     */       while (true) {
/* 430 */         if (pattern.equals(path))
/* 431 */           return true;
/* 432 */         int slash = path.lastIndexOf('/');
/* 433 */         if (slash <= 0)
/*     */           break;
/* 435 */         path = path.substring(0, slash);
/*     */       }
/* 437 */       return false;
/*     */     }
/*     */ 
/* 441 */     if (pattern.startsWith("*.")) {
/* 442 */       int slash = path.lastIndexOf('/');
/* 443 */       int period = path.lastIndexOf('.');
/*     */ 
/* 446 */       return (slash >= 0) && (period > slash) && (path.endsWith(pattern.substring(1)));
/*     */     }
/*     */ 
/* 453 */     return pattern.equals("/");
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.deploy.SecurityConstraint
 * JD-Core Version:    0.6.0
 */