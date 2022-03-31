package com.gamemz.mtodosnumricos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import android.content.pm.ActivityInfo;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FalsaPosicion extends AppCompatActivity {
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
        setContentView(R.layout.activity_falsa_posicion);
        function = findViewById(R.id.etxtFFuncion);
        xo = findViewById(R.id.etxtFXo);
        xu = findViewById(R.id.etxtFXu);
        eError = findViewById(R.id.etxtFError);
        calculate = findViewById(R.id.btnCalcularF);
        valoresX = findViewById(R.id.txvFValoresX);
        valoresE = findViewById(R.id.txvFValoresE);
        txvIteraciones = findViewById(R.id.txvNumF);
        resultado = findViewById(R.id.txvResultadoF);
        resultados = findViewById(R.id.cvIteracionesF);
        txvTitle = findViewById(R.id.txvTitleFP);
        txvTitle.setText("Falsa Posición");
        graphView = findViewById(R.id.fpGraph);
        scroll = findViewById(R.id.scrollFP);
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
                    //Definimos variables osea los getText de los cuadritos
                    String strFunction = function.getText().toString();
                    if(!strFunction.contains("x"))
                        throw new Exception();
                    double X1 = Double.parseDouble(xo.getText().toString());
                    double error = Double.parseDouble(eError.getText().toString());
                    double X2 = Double.parseDouble(xu.getText().toString());
                    ;
                    double X3 = 0;
                    double FX3 = 0;
                    String tablaIteraciones = "", errores = "";
                    int iteraciones = 0;
                    Expression function = fromStringToFunction(strFunction);

                    //Verificamos cambio de signo
                    if (function.setVariable("x", X1).evaluate() * function.setVariable("x", X2).evaluate() < 0) {
                        do {
                            //Aproximaciones
                            double FX1 = function.setVariable("x", X1).evaluate();
                            double FX2 = function.setVariable("x", X2).evaluate();
                            X3 = X1 - (FX1 * (X2 - X1)) / (FX2 - FX1);
                            FX3 = function.setVariable("x", X3).evaluate();
                            //Verificamos cambio de signo
                            if ((FX1 < 0 && FX3 > 0) || (FX1 > 0 && FX3 < 0)) {
                                X1 = X1;
                                X2 = X3;
                            } else if ((FX2 < 0 && FX3 > 0) || (FX2 > 0 && FX3 < 0)) {
                                X1 = X3;
                                X2 = X2;
                            }

                            iteraciones++;
                            tablaIteraciones += "#" + iteraciones + ": " + X3 + "\n";
                            errores += "#" + iteraciones + ": " + FX3 + "\n";
                            valoresI.add(X3);
                        } while (Math.abs(FX3) > error);
                    }
                    graph(function, iteraciones);
                    resultados.setVisibility(View.VISIBLE);
                    valoresE.setText(errores);
                    valoresX.setText(tablaIteraciones);
                    resultado.setText("" + X3);
                    txvIteraciones.setText("" + iteraciones);
                }
                catch(Exception e){
                    Toast t = Toast.makeText(getApplicationContext(), "Revisa los datos introducidos", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
    }
    public static Expression fromStringToFunction(String expression) {
        Expression e = new ExpressionBuilder(expression).variables("x").build();
        return e;
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