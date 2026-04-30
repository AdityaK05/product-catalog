import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.*;

class Product {
    final int id;
    final String name, category, image;
    final double price;

    Product(int id, String name, String category, double price, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
    }

    String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"category\":\"%s\",\"price\":%.2f,\"image\":\"%s\"}",
            id,
            name.replace("\"", "\\\""),
            category.replace("\"", "\\\""),
            price,
            image
        );
    }
}

public class ProductCatalog {

    static final List<Product> CATALOG = Arrays.asList(
        new Product(101, "Samsung Galaxy S24 Ultra",      "Electronics",  89999, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400&h=300&fit=crop"),
        new Product(102, "Apple MacBook Air M3",          "Electronics", 114999, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop"),
        new Product(103, "Sony WH-1000XM5 Headphones",   "Electronics",  24999, "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&h=300&fit=crop"),
        new Product(104, "LG 4K OLED TV 55\"",           "Electronics", 129999, "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=400&h=300&fit=crop"),
        new Product(105, "Boat Rockerz 450 BT",           "Electronics",   1299, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop"),
        new Product(106, "Canon EOS R50 Camera",          "Electronics",  64999, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&h=300&fit=crop"),
        new Product(201, "Allen Solly Formal Shirt",      "Clothing",      1799, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400&h=300&fit=crop"),
        new Product(202, "Levi's 511 Slim Jeans",         "Clothing",      2999, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=300&fit=crop"),
        new Product(203, "Nike Air Max 270",              "Clothing",      9999, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop"),
        new Product(301, "Clean Code – Robert Martin",    "Books",          499, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop"),
        new Product(302, "Design Patterns (GoF)",         "Books",          799, "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=300&fit=crop"),
        new Product(401, "Philips Air Fryer HD9252",      "Home & Kitchen", 6999, "https://images.unsplash.com/photo-1585515320310-259814833e62?w=400&h=300&fit=crop"),
        new Product(402, "Instant Pot Duo 7-in-1",        "Home & Kitchen", 8499, "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=300&fit=crop")
    );

    public static void main(String[] args) throws Exception {
        int port = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
        Server server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        ctx.addServlet(new ServletHolder(new ApiServlet()), "/api/*");
        ctx.addServlet(new ServletHolder(new UiServlet()), "/*");
        server.setHandler(ctx);
        server.start();
        System.out.println("✅  Server running at http://localhost:" + port);
        server.join();
    }

    // ── REST API  /api/products?sort=price|name&category=Electronics ──
    static class ApiServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            String sort = req.getParameter("sort");
            String cat  = req.getParameter("category");
            List<Product> list = new ArrayList<>(CATALOG);
            if (cat != null && !cat.isEmpty())
                list = list.stream().filter(p -> p.category.equalsIgnoreCase(cat)).collect(Collectors.toList());
            if ("name".equals(sort))
                list.sort(Comparator.comparing(p -> p.name.toLowerCase()));
            else if ("price".equals(sort))
                list.sort(Comparator.comparingDouble(p -> p.price));
            String json = "[" + list.stream().map(Product::toJson).collect(Collectors.joining(",")) + "]";
            res.getWriter().write(json);
        }
    }

    // ── Serves the single-page HTML app ──
    static class UiServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
            res.setContentType("text/html;charset=UTF-8");
            res.getWriter().write(HTML);
        }
    }

    static final String HTML =
"<!DOCTYPE html>" +
"<html lang='en'>" +
"<head>" +
"<meta charset='UTF-8'>" +
"<meta name='viewport' content='width=device-width,initial-scale=1'>" +
"<title>🛒 HoldIt – Product Catalog</title>" +
"<meta name='description' content='Browse, sort and filter the HoldIt product catalog – Electronics, Clothing, Books and Home & Kitchen.'>" +
"<link href='https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap' rel='stylesheet'>" +
"<style>" +
"*{margin:0;padding:0;box-sizing:border-box}" +
"body{font-family:'Inter',sans-serif;background:#060d1a;color:#e2e8f0;min-height:100vh}" +

/* ── HERO ── */
".hero{background:linear-gradient(135deg,#0f2a4a 0%,#060d1a 55%,#130f30 100%);padding:52px 40px 40px;border-bottom:1px solid rgba(255,255,255,.06);position:relative;overflow:hidden}" +
".hero::before{content:'';position:absolute;inset:0;background:radial-gradient(ellipse at 70% 50%,rgba(99,102,241,.15) 0%,transparent 60%);pointer-events:none}" +
".hero-inner{max-width:1200px;margin:auto;display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:20px;position:relative}" +
".brand{display:flex;align-items:center;gap:16px}" +
".brand-icon{width:56px;height:56px;background:linear-gradient(135deg,#6366f1,#8b5cf6);border-radius:16px;display:flex;align-items:center;justify-content:center;font-size:26px;box-shadow:0 6px 28px rgba(99,102,241,.45);flex-shrink:0}" +
".brand-text h1{font-size:30px;font-weight:800;background:linear-gradient(90deg,#a5b4fc,#c4b5fd);-webkit-background-clip:text;-webkit-text-fill-color:transparent;letter-spacing:-.5px}" +
".brand-text p{font-size:13px;color:#64748b;margin-top:3px;font-weight:400}" +
".hero-stats{display:flex;gap:12px;flex-wrap:wrap}" +
".stat-pill{background:rgba(99,102,241,.12);border:1px solid rgba(99,102,241,.25);color:#a5b4fc;padding:7px 18px;border-radius:999px;font-size:13px;font-weight:600;white-space:nowrap}" +

/* ── CONTROLS ── */
".controls{max-width:1200px;margin:28px auto 0;padding:0 40px;display:flex;flex-wrap:wrap;gap:10px;align-items:center}" +
".btn{padding:10px 22px;border:none;border-radius:12px;font-family:inherit;font-size:13px;font-weight:600;cursor:pointer;transition:all .22s cubic-bezier(.4,0,.2,1);display:inline-flex;align-items:center;gap:7px}" +
".btn-all{background:#1e293b;color:#94a3b8;border:1px solid #334155}" +
".btn-all:hover,.btn-all.active{background:#334155;color:#e2e8f0}" +
".btn-price{background:linear-gradient(135deg,#6366f1,#4f46e5);color:#fff;box-shadow:0 2px 14px rgba(99,102,241,.35)}" +
".btn-price:hover,.btn-price.active{transform:translateY(-2px);box-shadow:0 6px 24px rgba(99,102,241,.5)}" +
".btn-name{background:linear-gradient(135deg,#10b981,#059669);color:#fff;box-shadow:0 2px 14px rgba(16,185,129,.3)}" +
".btn-name:hover,.btn-name.active{transform:translateY(-2px);box-shadow:0 6px 24px rgba(16,185,129,.45)}" +
".btn-elec{background:linear-gradient(135deg,#f59e0b,#d97706);color:#fff;box-shadow:0 2px 14px rgba(245,158,11,.3)}" +
".btn-elec:hover,.btn-elec.active{transform:translateY(-2px);box-shadow:0 6px 24px rgba(245,158,11,.45)}" +
".btn-cloth{background:linear-gradient(135deg,#ec4899,#db2777);color:#fff;box-shadow:0 2px 14px rgba(236,72,153,.3)}" +
".btn-cloth:hover,.btn-cloth.active{transform:translateY(-2px);box-shadow:0 6px 24px rgba(236,72,153,.45)}" +
".search-wrap{margin-left:auto;position:relative}" +
".search-wrap input{background:#0f172a;border:1px solid #1e293b;color:#e2e8f0;padding:10px 16px 10px 40px;border-radius:12px;font-family:inherit;font-size:13px;width:240px;outline:none;transition:.2s}" +
".search-wrap input:focus{border-color:#6366f1;box-shadow:0 0 0 3px rgba(99,102,241,.18)}" +
".search-wrap .si{position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#64748b;font-size:15px;pointer-events:none}" +

/* ── STATUS BAR ── */
".status-bar{max-width:1200px;margin:20px auto 0;padding:0 40px;display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:8px}" +
".status-text{font-size:13px;color:#64748b}" +
".view-toggle{display:flex;gap:6px}" +
".view-btn{width:34px;height:34px;background:#0f172a;border:1px solid #1e293b;border-radius:8px;cursor:pointer;display:flex;align-items:center;justify-content:center;font-size:16px;color:#64748b;transition:.2s}" +
".view-btn.active,.view-btn:hover{background:#1e293b;color:#a5b4fc;border-color:#6366f1}" +

/* ── GRID ── */
".grid-wrap{max-width:1200px;margin:24px auto 60px;padding:0 40px}" +
".product-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(240px,1fr));gap:20px}" +

/* ── PRODUCT CARD ── */
".product-card{background:#0f172a;border:1px solid #1e293b;border-radius:18px;overflow:hidden;transition:all .28s cubic-bezier(.4,0,.2,1);cursor:pointer;animation:fadeUp .3s ease both}" +
".product-card:hover{transform:translateY(-6px);border-color:rgba(99,102,241,.4);box-shadow:0 16px 48px rgba(0,0,0,.5),0 0 0 1px rgba(99,102,241,.2)}" +
".card-img{width:100%;height:180px;object-fit:cover;display:block;background:#1e293b}" +
".card-body{padding:16px}" +
".card-cat{display:inline-block;padding:3px 10px;border-radius:6px;font-size:11px;font-weight:600;text-transform:uppercase;letter-spacing:.05em;margin-bottom:10px}" +
".cat-Electronics{background:rgba(99,102,241,.15);color:#a5b4fc}" +
".cat-Clothing{background:rgba(236,72,153,.15);color:#f9a8d4}" +
".cat-Books{background:rgba(245,158,11,.15);color:#fcd34d}" +
".cat-Home{background:rgba(16,185,129,.15);color:#6ee7b7}" +
".card-name{font-size:14px;font-weight:600;color:#e2e8f0;line-height:1.4;margin-bottom:12px;min-height:38px}" +
".card-footer{display:flex;align-items:center;justify-content:space-between}" +
".card-price{font-size:17px;font-weight:700;color:#6ee7b7}" +
".card-id{font-size:11px;color:#475569;font-family:monospace;background:#060d1a;padding:2px 8px;border-radius:5px;border:1px solid #1e293b}" +
".add-btn{background:linear-gradient(135deg,#6366f1,#8b5cf6);border:none;color:#fff;width:32px;height:32px;border-radius:9px;display:flex;align-items:center;justify-content:center;cursor:pointer;font-size:18px;transition:.2s;flex-shrink:0}" +
".add-btn:hover{transform:scale(1.12);box-shadow:0 4px 14px rgba(99,102,241,.5)}" +

/* ── TABLE (list view) ── */
".table-view{display:none}" +
".card-view .product-grid{display:grid}" +
".card-view .table-view{display:none}" +
".list-view .product-grid{display:none}" +
".list-view .table-view{display:block}" +
"table{width:100%;border-collapse:collapse;background:#0f172a;border:1px solid #1e293b;border-radius:16px;overflow:hidden}" +
"thead th{background:#060d1a;padding:14px 20px;text-align:left;font-size:11px;font-weight:600;text-transform:uppercase;letter-spacing:.06em;color:#64748b;white-space:nowrap}" +
"thead th:last-child{text-align:right}" +
"tbody tr{border-top:1px solid #1e293b;transition:background .15s;animation:fadeUp .2s ease both}" +
"tbody tr:hover{background:rgba(99,102,241,.05)}" +
"tbody td{padding:14px 20px;font-size:14px;vertical-align:middle}" +
"tbody td:last-child{text-align:right;font-weight:700;color:#6ee7b7}" +
".tbl-img{width:44px;height:44px;border-radius:8px;object-fit:cover;background:#1e293b}" +

/* ── MISC ── */
".empty{padding:80px;text-align:center;color:#475569;font-size:15px}" +
".spinner{width:36px;height:36px;border:3px solid #1e293b;border-top-color:#6366f1;border-radius:50%;animation:spin .7s linear infinite;margin:80px auto}" +
"@keyframes spin{to{transform:rotate(360deg)}}" +
"@keyframes fadeUp{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:translateY(0)}}" +
"@media(max-width:700px){" +
"  .hero,.controls,.status-bar,.grid-wrap{padding-left:16px;padding-right:16px}" +
"  .search-wrap{width:100%;margin-left:0}.search-wrap input{width:100%}" +
"  .product-grid{grid-template-columns:1fr 1fr}" +
"}" +
"@media(max-width:440px){.product-grid{grid-template-columns:1fr}}" +
"</style></head><body class='card-view'>" +

/* ── HERO ── */
"<div class='hero'><div class='hero-inner'>" +
"<div class='brand'><div class='brand-icon'>🛍</div><div class='brand-text'>" +
"<h1>HoldIt Store</h1><p>Browse · Sort · Filter · Discover</p></div></div>" +
"<div class='hero-stats'>" +
"<span class='stat-pill' id='totalBadge'>Loading...</span>" +
"<span class='stat-pill' id='catBadge' style='display:none'></span>" +
"</div>" +
"</div></div>" +

/* ── CONTROLS ── */
"<div class='controls'>" +
"<button class='btn btn-all active' id='btnAll'    onclick='load(null,null,this)'>📋 All</button>" +
"<button class='btn btn-price'      id='btnPrice'  onclick='load(\"price\",null,this)'>💰 By Price</button>" +
"<button class='btn btn-name'       id='btnName'   onclick='load(\"name\",null,this)'>🔤 By Name</button>" +
"<button class='btn btn-elec'       id='btnElec'   onclick='load(null,\"Electronics\",this)'>⚡ Electronics</button>" +
"<button class='btn btn-cloth'      id='btnCloth'  onclick='load(null,\"Clothing\",this)'>👗 Clothing</button>" +
"<div class='search-wrap'><span class='si'>🔍</span>" +
"<input id='search' type='text' placeholder='Search products...' oninput='filterCards()'>" +
"</div>" +
"</div>" +

/* ── STATUS / VIEW TOGGLE ── */
"<div class='status-bar'>" +
"<span class='status-text' id='statusText'></span>" +
"<div class='view-toggle'>" +
"<button class='view-btn active' id='vGrid' title='Grid view' onclick='setView(\"card\",this)'>⊞</button>" +
"<button class='view-btn'        id='vList' title='List view' onclick='setView(\"list\",this)'>≡</button>" +
"</div>" +
"</div>" +

/* ── GRID / TABLE ── */
"<div class='grid-wrap'>" +
"<div class='product-grid' id='productGrid'><div class='spinner'></div></div>" +
"<div class='table-view' id='productTable'></div>" +
"</div>" +

"<script>" +
"let allData=[];" +
"let activeBtn=document.getElementById('btnAll');" +
"function catClass(c){" +
"  if(c.startsWith('Home'))return'cat-Home';" +
"  return'cat-'+c.replace(/[^a-zA-Z]/g,'');" +
"}" +
"function fmt(p){return'₹ '+p.toLocaleString('en-IN',{minimumFractionDigits:2});}" +
"async function load(sort,cat,btn){" +
"  if(activeBtn){activeBtn.classList.remove('active');}" +
"  if(btn){btn.classList.add('active');activeBtn=btn;}" +
"  document.getElementById('productGrid').innerHTML=\"<div class='spinner'></div>\";" +
"  document.getElementById('productTable').innerHTML='';" +
"  let url='/api/products?x=1';" +
"  if(sort)url+='&sort='+sort;" +
"  if(cat)url+='&category='+encodeURIComponent(cat);" +
"  const data=await fetch(url).then(r=>r.json());" +
"  allData=data;" +
"  render(data,sort,cat);" +
"}" +
"function render(data,sort,cat){" +
"  const n=data.length;" +
"  document.getElementById('totalBadge').textContent=n+' product'+(n!==1?'s':'');" +
"  let catEl=document.getElementById('catBadge');" +
"  if(cat){catEl.textContent=cat;catEl.style.display='';}else{catEl.style.display='none';}" +
"  let msg='Showing all '+n+' products';" +
"  if(sort==='price')msg='Sorted by price ↑ · '+n+' products';" +
"  else if(sort==='name')msg='Sorted A → Z · '+n+' products';" +
"  else if(cat)msg='Category: '+cat+' · '+n+' products';" +
"  document.getElementById('statusText').textContent=msg;" +
"  renderGrid(data);" +
"  renderTable(data);" +
"}" +
"function renderGrid(data){" +
"  let g=document.getElementById('productGrid');" +
"  if(!data.length){g.innerHTML=\"<div class='empty'>😕 No products found</div>\";return;}" +
"  g.innerHTML=data.map((p,i)=>" +
"    `<div class='product-card' style='animation-delay:${i*30}ms'>" +
"      <img class='card-img' src='${p.image}' alt='${p.name}' loading='lazy' onerror=\"this.src='https://placehold.co/400x300/1e293b/64748b?text=No+Image'\">" +
"      <div class='card-body'>" +
"        <span class='card-cat ${catClass(p.category)}'>${p.category}</span>" +
"        <div class='card-name'>${p.name}</div>" +
"        <div class='card-footer'>" +
"          <span class='card-price'>${fmt(p.price)}</span>" +
"          <span class='card-id'>#${p.id}</span>" +
"        </div>" +
"      </div>" +
"    </div>`" +
"  ).join('');" +
"}" +
"function renderTable(data){" +
"  let t=document.getElementById('productTable');" +
"  if(!data.length){t.innerHTML=\"<div class='empty'>😕 No products found</div>\";return;}" +
"  let h=\"<table><thead><tr><th></th><th>ID</th><th>Product Name</th><th>Category</th><th>Price</th></tr></thead><tbody>\";" +
"  data.forEach((p,i)=>{" +
"    h+=`<tr style='animation-delay:${i*20}ms'>" +
"      <td><img class='tbl-img' src='${p.image}' alt='' loading='lazy' onerror=\"this.src='https://placehold.co/44x44/1e293b/64748b?text=?'\"></td>" +
"      <td style='font-family:monospace;color:#64748b;font-size:12px'>#${p.id}</td>" +
"      <td style='font-weight:500'>${p.name}</td>" +
"      <td><span class='card-cat ${catClass(p.category)}'>${p.category}</span></td>" +
"      <td>${fmt(p.price)}</td>" +
"    </tr>`;" +
"  });" +
"  h+='</tbody></table>';" +
"  t.innerHTML=h;" +
"}" +
"function filterCards(){" +
"  const q=document.getElementById('search').value.toLowerCase();" +
"  const f=allData.filter(p=>p.name.toLowerCase().includes(q)||p.category.toLowerCase().includes(q));" +
"  render(f,null,null);" +
"  document.getElementById('statusText').textContent='Showing '+f.length+' of '+allData.length+' products';" +
"}" +
"function setView(mode,btn){" +
"  document.body.className=(mode==='list'?'list-view':'card-view');" +
"  document.querySelectorAll('.view-btn').forEach(b=>b.classList.remove('active'));" +
"  btn.classList.add('active');" +
"}" +
"load();" +
"</script></body></html>";
}
