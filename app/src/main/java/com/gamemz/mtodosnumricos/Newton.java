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

public class Newton extends AppCompatActivity {
    Button calcular;
    EditText funcion, intervalError, xo;
    TextView result, iterations, errors, noIterations, txvTitle;
    CardView cvNR;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    ScrollView scroll;

    List<Double> valoresI = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_newton);

        calcular = findViewById(R.id.btnCalcularB);
        funcion = findViewById(R.id.etxtBFuncion);
        intervalError = findViewById(R.id.etxtBError);
        xo = findViewById(R.id.etxtBXu);
        result = findViewById(R.id.txvResultadoB);
        iterations = findViewById(R.id.txvBValoresX);
        errors = findViewById(R.id.txvBValoresE);
        noIterations = findViewById(R.id.txvNumB);
        cvNR = findViewById(R.id.cvIteracionesB);
        txvTitle = findViewById(R.id.txvTitleN);
        txvTitle.setText("Newton Raphson");
        graphView = findViewById(R.id.newtonGraph);
        scroll = findViewById(R.id.scrollN);

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

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String strFunction = funcion.getText().toString();
                    if(!strFunction.contains("x"))
                        throw new Exception();
                    double Xn = Double.parseDouble(xo.getText().toString());
                    double error = Double.parseDouble(intervalError.getText().toString());
                    double Xn2 = 0;
                    String tablaIteraciones = "", errores="";
                    int iteraciones = 0;
                    Expression function = fromStringToFunction(strFunction);
                    double derivada;
                    do{
                        derivada = derive(function, Xn);
                        function.setVariable("x", Xn);
                        Xn2 = Xn - (function.evaluate() / derivada);
                        Xn = Xn2;
                        valoresI.add(Xn);
                        iteraciones++;
                        tablaIteraciones+="#"+iteraciones+": "+Xn+"\n";
                        errores+="#"+iteraciones+": "+function.setVariable("x",Xn).evaluate()+"\n";
                    } while(Math.abs(function.setVariable("x",Xn).evaluate()) > error);
                    graph(function, iteraciones);
                    result.setText(""+Xn);
                    iterations.setText(tablaIteraciones);
                    errors.setText(errores);
                    noIterations.setText(""+iteraciones);
                    cvNR.setVisibility(View.VISIBLE);
                } catch(Exception e){
                    Toast t = Toast.makeText(getApplicationContext(), "Revisa los datos introducidos\n"+e, Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }
    public static Expression fromStringToFunction(String expression) {
        Expression e = new ExpressionBuilder(expression)
                .variables("x")
                .build();
        return e;
    }
    private static final double DX = 0.00000001;
    public static double derive(Expression f, double xo) {
        double parteUno, parteDos;
        f.setVariable("x", xo+DX);
        parteUno = f.evaluate();
        f.setVariable("x", xo);
        parteDos = f.evaluate();
        return (parteUno - parteDos) / DX;
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