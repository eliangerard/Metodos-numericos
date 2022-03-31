package com.gamemz.mtodosnumricos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Secante extends AppCompatActivity {
    EditText function, xo, xu, eError;
    Button calculate;
    TextView valoresX, valoresE, txvIteraciones,resultado, txvTitle;
    CardView resultados;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    ScrollView scroll;
    List<Double> valoresI = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_secante);
        function = findViewById(R.id.etxtBFuncion);
        xo = findViewById(R.id.etxtBXo);
        xu = findViewById(R.id.etxtBXu);
        eError = findViewById(R.id.etxtBError);
        calculate = findViewById(R.id.btnCalcularB);
        valoresX = findViewById(R.id.txvBValoresX);
        valoresE = findViewById(R.id.txvBValoresE);
        txvIteraciones = findViewById(R.id.txvNumB);
        resultado = findViewById(R.id.txvResultadoB);
        resultados = findViewById(R.id.cvIteracionesB);
        txvTitle = findViewById(R.id.txvTitleB);
        txvTitle.setText("Secante");
        graphView = findViewById(R.id.bGraph);
        scroll = findViewById(R.id.scrollB);

        graphView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    scroll.requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    scroll.requestDisallowInterceptTouchEvent(false);
                    return true;
                }
                return false;
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String funcion = "", iteraciones = "", valores = "";
                    double error, x0, x1;
                    int contador = 1;
                    double x2 = 100000;

                    funcion = function.getText().toString();
                    if (!funcion.contains("x"))
                        throw new Exception();
                    x0 = Double.parseDouble(xo.getText().toString());
                    x1 = Double.parseDouble(xu.getText().toString());
                    error = Double.parseDouble(eError.getText().toString());

                    ExpressionBuilder e = new ExpressionBuilder(funcion);
                    e.variable("x");
                    Expression ex0 = e.build();
                    ex0.setVariable("x", x0);
                    Expression ex1 = e.build();
                    ex1.setVariable("x", x1);

                    x2 = x1 - ((ex1.evaluate() * (x0 - x1)) / (ex0.evaluate() - ex1.evaluate()));
                    Expression exf = e.build();
                    exf.setVariable("x", x2);
                    valores += "#" + contador + ": " + x2 + "\n";
                    iteraciones += "#" + contador + ": " + exf.evaluate() + "\n";
                    valoresI.add(x2);
                    while (Math.abs(exf.evaluate()) > error) {
                        x0 = x1;
                        x1 = x2;
                        ex0.setVariable("x", x0);
                        ex1.setVariable("x", x1);
                        x2 = x1 - ((ex1.evaluate() * (x0 - x1)) / (ex0.evaluate() - ex1.evaluate()));
                        exf.setVariable("x", x2);
                        contador++;
                        valores += "#" + contador + ": " + x2 + "\n";
                        iteraciones += "#" + contador + ": " + exf.evaluate() + "\n";
                        valoresI.add(x2);
                    }
                    graph(e.build(), contador);
                    resultados.setVisibility(View.VISIBLE);
                    resultado.setText("" + x2);
                    txvIteraciones.setText("" + contador);
                    valoresE.setText(iteraciones);
                    valoresX.setText(valores);
                }
                catch (Exception e){
                    Toast t = Toast.makeText(getApplicationContext(), "Revisa los datos introducidos", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }
    public void graph(Expression fx, int iteraciones){
        graphView.removeAllSeries();
        double x = -25,y = 0;
        series = new LineGraphSeries<>();
        PointsGraphSeries<DataPoint> puntos = new PointsGraphSeries<>();
        Collections.sort(valoresI);
        for(int i = 0; i<iteraciones; i++){
            puntos.appendData(new DataPoint(valoresI.get(i),fx.setVariable("x", valoresI.get(i)).evaluate()), true, iteraciones);
        }
        graphView.addSeries(puntos);
        for(int i = 0; i<500; i++){
            x +=0.1;
            y = fx.setVariable("x",x).evaluate();
            series.appendData(new DataPoint(x,y),true,500);
        }
        graphView.addSeries(series);
        graphView.getViewport().setScalableY(true);
    }
}