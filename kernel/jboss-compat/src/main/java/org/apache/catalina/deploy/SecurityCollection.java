/*     */ package org.apache.catalina.deploy;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.catalina.util.RequestUtil;
/*     */ import org.jboss.logging.Logger;
/*     */ 
/*     */ public class SecurityCollection
/*     */   implements Serializable
/*     */ {
/*  46 */   private static Logger log = Logger.getLogger(SecurityCollection.class);
/*     */ 
/*  95 */   private String description = null;
/*     */ 
/* 101 */   private String[] methods = new String[0];
/*     */ 
/* 107 */   private String name = null;
/*     */ 
/* 113 */   private String[] patterns = new String[0];
/*     */ 
/*     */   public SecurityCollection()
/*     */   {
/*  57 */     this(null, null);
/*     */   }
/*     */ 
/*     */   public SecurityCollection(String name)
/*     */   {
/*  69 */     this(name, null);
/*     */   }
/*     */ 
/*     */   public SecurityCollection(String name, String description)
/*     */   {
/*  83 */     setName(name);
/*  84 */     setDescription(description);
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 124 */     return this.description;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description)
/*     */   {
/* 136 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 146 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 158 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void addMethod(String method)
/*     */   {
/* 171 */     if (method == null)
/* 172 */       return;
/* 173 */     String[] results = new String[this.methods.length + 1];
/* 174 */     for (int i = 0; i < this.methods.length; i++)
/* 175 */       results[i] = this.methods[i];
/* 176 */     results[this.methods.length] = method;
/* 177 */     this.methods = results;
/*     */   }
/*     */ 
/*     */   public void addPattern(String pattern)
/*     */   {
/* 187 */     if (pattern == null) {
/* 188 */       return;
/*     */     }
/*     */ 
/* 191 */     if ((pattern.endsWith("*")) && 
/* 192 */       (pattern.charAt(pattern.length() - 1) != '/') && 
/* 193 */       (log.isDebugEnabled())) {
/* 194 */       log.warn("Suspicious url pattern: \"" + pattern + "\"" + " - see http://java.sun.com/aboutJava/communityprocess/first/jsr053/servlet23_PFD.pdf" + "  section 11.2");
/*     */     }
/*     */ 
/* 201 */     pattern = RequestUtil.URLDecode(pattern);
/* 202 */     String[] results = new String[this.patterns.length + 1];
/* 203 */     for (int i = 0; i < this.patterns.length; i++) {
/* 204 */       results[i] = this.patterns[i];
/*     */     }
/* 206 */     results[this.patterns.length] = pattern;
/* 207 */     this.patterns = results;
/*     */   }
/*     */ 
/*     */   public boolean findMethod(String method)
/*     */   {
/* 220 */     if (this.methods.length == 0)
/* 221 */       return true;
/* 222 */     for (int i = 0; i < this.methods.length; i++) {
/* 223 */       if (this.methods[i].equals(method))
/* 224 */         return true;
/*     */     }
/* 226 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] findMethods()
/*     */   {
/* 238 */     return this.methods;
/*     */   }
/*     */ 
/*     */   public boolean findPattern(String pattern)
/*     */   {
/* 250 */     for (int i = 0; i < this.patterns.length; i++) {
/* 251 */       if (this.patterns[i].equals(pattern))
/* 252 */         return true;
/*     */     }
/* 254 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] findPatterns()
/*     */   {
/* 266 */     return this.patterns;
/*     */   }
/*     */ 
/*     */   public void removeMethod(String method)
/*     */   {
/* 279 */     if (method == null)
/* 280 */       return;
/* 281 */     int n = -1;
/* 282 */     for (int i = 0; i < this.methods.length; i++) {
/* 283 */       if (this.methods[i].equals(method)) {
/* 284 */         n = i;
/* 285 */         break;
/*     */       }
/*     */     }
/* 288 */     if (n >= 0) {
/* 289 */       int j = 0;
/* 290 */       String[] results = new String[this.methods.length - 1];
/* 291 */       for (int i = 0; i < this.methods.length; i++) {
/* 292 */         if (i != n)
/* 293 */           results[(j++)] = this.methods[i];
/*     */       }
/* 295 */       this.methods = results;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removePattern(String pattern)
/*     */   {
/* 309 */     if (pattern == null)
/* 310 */       return;
/* 311 */     int n = -1;
/* 312 */     for (int i = 0; i < this.patterns.length; i++) {
/* 313 */       if (this.patterns[i].equals(pattern)) {
/* 314 */         n = i;
/* 315 */         break;
/*     */       }
/*     */     }
/* 318 */     if (n >= 0) {
/* 319 */       int j = 0;
/* 320 */       String[] results = new String[this.patterns.length - 1];
/* 321 */       for (int i = 0; i < this.patterns.length; i++) {
/* 322 */         if (i != n)
/* 323 */           results[(j++)] = this.patterns[i];
/*     */       }
/* 325 */       this.patterns = results;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 336 */     StringBuffer sb = new StringBuffer("SecurityCollection[");
/* 337 */     sb.append(this.name);
/* 338 */     if (this.description != null) {
/* 339 */       sb.append(", ");
/* 340 */       sb.append(this.description);
/*     */     }
/* 342 */     sb.append("]");
/* 343 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.deploy.SecurityCollection
 * JD-Core Version:    0.6.0
 */