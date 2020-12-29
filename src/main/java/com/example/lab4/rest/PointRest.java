package com.example.lab4.rest;

import com.example.lab4.db.PointRepositoryJPA;
import com.example.lab4.models.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:9824")
@RestController
@RequestMapping("/api")
public class PointRest {
    PointRepositoryJPA dbPoints;

    @Autowired
    Session session;

    @Autowired
    public PointRest(PointRepositoryJPA dbPoints) {
        this.dbPoints = dbPoints;
    }

    @PostMapping(value="/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyResponse> add(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = "failed";


        if (session.isValidUser(request.getKey())) {
            try {
                Double x = request.getX();
                Double y = request.getY();
                Double r = request.getR();

                if (r.toString().length() > 4 || r <= 0 || r > 4.99)
                    throw new Exception("Invalid r");

                Point point = new Point();
                point.setX(x);
                point.setY(y);
                point.setR(r);
                point.setResult(calculate(x, y, r));
                this.dbPoints.save(point);
                resp.last_point = point;
                resp.status = "ok";
            } catch (Exception e) {
                resp.status = "failed";
            }
        } else {
            resp.status = "failed";
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value="/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyResponse> results(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = "failed";


        if (session.isValidUser(request.getKey())) {
            resp.data = this.dbPoints.findAll();
            resp.status = "ok";
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/clear")
    public ResponseEntity<MyResponse> clear(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = "failed";

        if (session.isValidUser(request.getKey())) {
            this.dbPoints.deleteAll();
            resp.status = "ok";
        }
        return ResponseEntity.ok(resp);
    }

    public static Boolean calculate(Double x, Double y, Double r) {
        if (x >= 0 && y >= 0 && x*x + y*y <= r*r/4)
            return true;
        else if (x <= 0 && y >= 0 && y <= r/2 && x >= -r)
            return true;
        else if (x >= 0 && y <= 0 && y >= x - r/2)
            return true;
        else
            return false;
    }
}
